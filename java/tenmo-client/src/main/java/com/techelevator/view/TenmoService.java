package com.techelevator.view;

import com.techelevator.tenmo.model.TransferDTO;
import okhttp3.Response;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.print.DocFlavor;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * New created class, a lot of this comes from PokemonService in the client code from Tuesday
 */
public class TenmoService {
    private String baseUrl;
    private RestTemplate restTemplate = new RestTemplate();
    private String authToken;

    public TenmoService(String url){
        baseUrl = url;
    }

    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }


    //Needs a re-work without id in URL after server-side change
    public BigDecimal getCurrentBalance(){
        ResponseEntity<BigDecimal> response = restTemplate.exchange(baseUrl + "account", HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
        return response.getBody();
    }

    public long getUserIdByName(){
        ResponseEntity<Long> response = restTemplate.exchange(baseUrl + "user", HttpMethod.GET, makeAuthEntity(), Long.class );
        return response.getBody();
    }

    public String getUsersList(){
     ResponseEntity<String> response = restTemplate.exchange(baseUrl + "transfer", HttpMethod.GET, makeAuthEntity(), String.class);
     return response.getBody();
    }


    /*public List<TransferDTO> getTransfersList(){
        ResponseEntity<List<TransferDTO>> response = restTemplate.exchange(baseUrl+ "transfers", HttpMethod.GET, makeAuthEntity(), List<TransferDTO>);
    }*/

    public List<TransferDTO> getTransfersList(){
        List<TransferDTO> transfers = null;
        HttpEntity<Void> entity = makeAuthEntity();
        ResponseEntity<TransferDTO[]> response = restTemplate.exchange(baseUrl + "transfers", HttpMethod.GET, entity, TransferDTO[].class);
        transfers = new ArrayList<>(Arrays.asList(response.getBody()));
        return transfers;
    }

    public List<TransferDTO> getRequestsList(){
        List<TransferDTO> requests = null;
        HttpEntity<Void> entity = makeAuthEntity();
        ResponseEntity<TransferDTO[]> response = restTemplate.exchange(baseUrl + "request", HttpMethod.GET, entity, TransferDTO[].class);
        requests = new ArrayList<>(Arrays.asList(response.getBody()));
        return requests;
    }

    public String makeTransfer(TransferDTO transferDTO){
        return restTemplate.postForObject(baseUrl + "transfer", makeTransferEntity(transferDTO), String.class);
    }

    public String makeRequest(TransferDTO transferDTO){
        ;
        return restTemplate.postForObject(baseUrl + "request", makeTransferEntity(transferDTO), String.class);
    }

    public String approveRequest(TransferDTO transferDTO){
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "request", HttpMethod.PUT, makeTransferEntity(transferDTO), String.class);
        return response.getBody();
    }

    public String rejectRequest(TransferDTO transferDTO){
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "reject", HttpMethod.PUT, makeTransferEntity(transferDTO), String.class);
        return  response.getBody();
    }


    private HttpEntity<TransferDTO> makeTransferEntity(TransferDTO transferDTO){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transferDTO, headers);
    }

    public HttpEntity<Void> makeAuthEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }
}
