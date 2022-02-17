package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDTO;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    public boolean sufficientFunds(Long userId, BigDecimal amountToTransfer);

    String makeTransfer(TransferDTO transferDTO);

    public List<TransferDTO> listTransfers(long userId);

    public String makeRequest(TransferDTO transferDTO);

    public List<TransferDTO> listRequests(long userId);

    public String approveRequest(TransferDTO transferDTO);

    public String rejectRequest(TransferDTO transferDTO);
}
