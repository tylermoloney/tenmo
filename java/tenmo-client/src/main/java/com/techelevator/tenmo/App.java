package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.TransferDTO;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;
import com.techelevator.view.TenmoService;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
    private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private TenmoService tenmoService;


    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new TenmoService(API_BASE_URL));
        app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, TenmoService tenmoService) {
        this.console = console;
        this.authenticationService = authenticationService;
        this.tenmoService = tenmoService;

    }

    public void run() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");

        registerAndLogin();
        mainMenu();
    }

    private void mainMenu() {
        while (true) {
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                viewCurrentBalance();
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                viewTransferHistory();
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                sendBucks();
            } else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
                requestBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }

    private void viewCurrentBalance() {
        //Also didn't see somewhere in ConsoleService to do this, maybe we make a simple displayMessage() or something

        System.out.println("Your current balance is: " + tenmoService.getCurrentBalance());


    }

    private void viewTransferHistory() {
        boolean viewingTransfers = true;
        while (viewingTransfers) {
            List<TransferDTO> transfers = tenmoService.getTransfersList();
            System.out.println("-------------------------------------------");
            System.out.println("Transfer History");
            System.out.println("ID      From/To          Amount");
            System.out.println("-------------------------------------------");


            for (int i = 0; i < transfers.size(); i++) {


                if (transfers.get(i).getUsernameFrom().equals(currentUser.getUser().getUsername())) {
                    System.out.println(transfers.get(i).getTransferId() + " To:   " + transfers.get(i).getUsernameTo() + "         " + transfers.get(i).getAmount());
                } else {
                    System.out.println(transfers.get(i).getTransferId() + " From: " + transfers.get(i).getUsernameFrom() + "         " + transfers.get(i).getAmount());
                }


            }
            //method to retrieve transfer details here:
            int userSelection = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel) ");
            if (userSelection == 0) {
                viewingTransfers = false;
            }

            for (int i = 0; i < transfers.size(); i++) {
                if (transfers.get(i).getTransferId() == userSelection) {
                    TransferDTO dto = transfers.get(i);
                    System.out.println("-------------------------------------------");
                    System.out.println("Transfer Details");
                    System.out.println("-------------------------------------------");
                    System.out.println(dto.toString());
                    System.out.println("-------------------------------------------");
                }
            }


            viewingTransfers = false;
        }
    }

    private void viewPendingRequests() {
        boolean viewingRequests = true;
        while (viewingRequests) {
            List<TransferDTO> requestsList = tenmoService.getRequestsList();
            if (requestsList.size() == 0) {
                System.out.println("No pending requests");
                viewingRequests = false;
            } else {
                System.out.println("-------------------------------------------");
                System.out.println("Pending Transfers:");
                System.out.println("ID      To:      Amount:");
                for (int i = 0; i < requestsList.size(); i++) {
                    System.out.println(requestsList.get(i).getTransferId() + "     " + requestsList.get(i).getUsernameTo() + "     " + requestsList.get(i).getAmount());

                }
                System.out.println("-------------------------------------------");
                int userSelection = console.getUserInputInteger("Please enter transfer ID to approve/reject (0 to cancel)");
                if (userSelection == 0) {
                    viewingRequests = false;
                }
                for (int i = 0; i < requestsList.size(); i++) {
                    if (requestsList.get(i).getTransferId() == userSelection) {
                        TransferDTO dto = requestsList.get(i);
                        System.out.println("-------------------------------------------");
                        System.out.println("Request Details");
                        System.out.println("-------------------------------------------");
                        System.out.println(dto.toString());
                        System.out.println("-------------------------------------------");
                        int approveSelection = console.getUserInputInteger("1. Approve \n2. Reject \n0. Don't approve or reject");
                        if (approveSelection == 0) {
                            viewingRequests = false;
                        }
                        if (approveSelection == 1) {
                            System.out.println(tenmoService.approveRequest(dto));
                            viewingRequests = false;
                        }
                        if (approveSelection == 2) {
                            System.out.println(tenmoService.rejectRequest(dto));
                            viewingRequests = false;
                        } if(approveSelection != 0 && approveSelection != 1 && approveSelection != 2) {
                            System.out.println("Invalid selection.");
                        }
                    }
                }


            }
        }

    }

    private void sendBucks() {
        // TODO Auto-generated method stub
        boolean makingTransfer = true;
        while (makingTransfer) {
            System.out.println("-------------------------------------------");
            System.out.println("ID:  Name:");
            System.out.println(tenmoService.getUsersList());
            System.out.println("-------------------------------------------");
            int userSelection = console.getUserInputInteger("Please enter user ID to transfer to");
            BigDecimal amountToTransfer = new BigDecimal(console.getUserInput("Please enter the amount to transfer"));
            TransferDTO transferDTO = new TransferDTO();
            transferDTO.setAmount(amountToTransfer);
            transferDTO.setUserTo(userSelection);
            System.out.println(tenmoService.makeTransfer(transferDTO));
            makingTransfer = false;


        }
    }

    private void requestBucks() {
        // TODO Auto-generated method stub
        boolean makingRequest = true;
        while (makingRequest) {
            System.out.println("-------------------------------------------");
            System.out.println("ID:  Name:");
            System.out.println(tenmoService.getUsersList());
            System.out.println("-------------------------------------------");
            int userSelection = console.getUserInputInteger("Please enter user ID to request from");
            BigDecimal amountToRequest = new BigDecimal(console.getUserInput("Please enter the amount to request"));
            TransferDTO transferDTO = new TransferDTO();
            transferDTO.setAmount(amountToRequest);
            transferDTO.setUserFrom(userSelection);
            transferDTO.setUserTo(currentUser.getUser().getId());
            System.out.println(tenmoService.makeRequest(transferDTO));
            makingRequest = false;
        }

    }

    private void exitProgram() {
        System.exit(0);
    }

    private void registerAndLogin() {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }

    private boolean isAuthenticated() {
        return currentUser != null;
    }

    private void register() {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                authenticationService.register(credentials);
                isRegistered = true;
                System.out.println("Registration successful. You can now login.");
            } catch (AuthenticationServiceException e) {
                System.out.println("REGISTRATION ERROR: " + e.getMessage());
                System.out.println("Please attempt to register again.");
            }
        }
    }

    private void login() {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            UserCredentials credentials = collectUserCredentials();
            try {
                currentUser = authenticationService.login(credentials);
                //Added below to be able to add AuthTokens to authenticated methods
                tenmoService.setAuthToken(currentUser.getToken());
            } catch (AuthenticationServiceException e) {
                System.out.println("LOGIN ERROR: " + e.getMessage());
                System.out.println("Please attempt to login again.");
            }
        }
    }

    private UserCredentials collectUserCredentials() {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }
}
