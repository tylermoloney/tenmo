package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.TransferDTO;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {


    @Autowired
    private AccountDao accountDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TransferDao transferDao;


    @GetMapping("/account")
    public BigDecimal getAccountBalanceByUserId(Principal principal) {
        long userId = getUserId(principal);
        Account account = accountDao.getAccountByUserId(userId);
        return account.getAccountBalance();
    }


//    @RequestMapping(path = "/account/{id}", method = RequestMethod.GET)
//    public BigDecimal get(@PathVariable Long id) {
//        return accountDao.getBalance(id);
//    }

    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public long getUserId(Principal principal) {
        String username = principal.getName();
        return userDao.findIdByUsername(username);
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.GET)
    public String filterList(Principal principal) {
        String returnString = "";
        List<User> filteredList = new ArrayList<>();
        String requestingUser = principal.getName();
        List<User> users = userDao.findAll();
        for (int i = 0; i < users.size(); i++) {
            if (!users.get(i).getUsername().equals(requestingUser)) {
                Long id = users.get(i).getId();
                String username = users.get(i).getUsername();
                returnString += (id + " " + username + "\n");
            }
        }
        return returnString;
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public String makeTransfer(@Valid Principal principal, @RequestBody TransferDTO transferDTO) {
        transferDTO.setUserFrom(getUserId(principal));
        if (transferDao.sufficientFunds(transferDTO.getUserFrom(), transferDTO.getAmount())) {
            return transferDao.makeTransfer(transferDTO);
        }
        return "insufficient funds";

    }

    @RequestMapping(path = "/transfers", method = RequestMethod.GET)
    public List<TransferDTO> listTransfers(Principal principal) {
        return transferDao.listTransfers(getUserId(principal));
    }

    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public String makeRequest (@Valid Principal principal, @RequestBody TransferDTO transferDTO) {
        transferDTO.setUserTo(getUserId(principal));
        return transferDao.makeRequest(transferDTO);
    }

    @RequestMapping(path = "/request", method = RequestMethod.GET)
    public List<TransferDTO> listRequests(Principal principal){
        return transferDao.listRequests(getUserId(principal));
    }

    @RequestMapping(path = "/request", method = RequestMethod.PUT)
    public String approveRequest(@Valid Principal principal, @RequestBody TransferDTO transferDTO){
        if (transferDao.sufficientFunds(transferDTO.getUserFrom(), transferDTO.getAmount())) {
            return transferDao.approveRequest(transferDTO);

        }
        return "insufficient funds";
    }

    @RequestMapping(path = "/reject", method = RequestMethod.PUT)
    public String rejectRequest(@Valid Principal principal, @RequestBody TransferDTO transferDTO){
        return transferDao.rejectRequest(transferDTO);
    }


}
