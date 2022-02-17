package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{
    private JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    private JdbcAccountDao accountDao;
    private JdbcUserDao userDao;

    @Override
    public boolean sufficientFunds(Long userId, BigDecimal amountToTransfer) {
        String sql = "SELECT balance FROM accounts WHERE user_id = ?";
        BigDecimal userBalance = jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
        return (userBalance.compareTo(amountToTransfer) >= 0);

    }

    @Override
    public String makeTransfer(TransferDTO transferDTO){
        long accountFromId = jdbcTemplate.queryForObject( "SELECT account_id FROM accounts WHERE user_id = ?", Long.class ,transferDTO.getUserFrom());
        long accountToId = jdbcTemplate.queryForObject( "SELECT account_id FROM accounts WHERE user_id = ?", Long.class ,transferDTO.getUserTo());
        String sql = "INSERT INTO transfers (account_from, account_to, amount, transfer_type_id, transfer_status_id) VALUES (? , ? , ?, 2, 2)";
        jdbcTemplate.update(sql, accountFromId, accountToId, transferDTO.getAmount());

        sql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
        jdbcTemplate.update(sql,transferDTO.getAmount(), accountFromId);

        sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        jdbcTemplate.update(sql, transferDTO.getAmount(), accountToId);
        return "Transfer successful!";

    }

    @Override
    public String makeRequest(TransferDTO transferDTO){
        long accountFromId = jdbcTemplate.queryForObject( "SELECT account_id FROM accounts WHERE user_id = ?", Long.class ,transferDTO.getUserFrom());
        long accountToId = jdbcTemplate.queryForObject( "SELECT account_id FROM accounts WHERE user_id = ?", Long.class ,transferDTO.getUserTo());
        String sql = "INSERT INTO transfers (account_from, account_to, amount, transfer_type_id, transfer_status_id) VALUES (? , ? , ?, 1, 1)";
        jdbcTemplate.update(sql, accountFromId, accountToId, transferDTO.getAmount());
        return "Request Made";
    }

    @Override
    public String approveRequest(TransferDTO transferDTO){
        long accountFromId = jdbcTemplate.queryForObject( "SELECT account_id FROM accounts WHERE user_id = ?", Long.class ,transferDTO.getUserFrom());
        long accountToId = jdbcTemplate.queryForObject( "SELECT account_id FROM accounts WHERE user_id = ?", Long.class ,transferDTO.getUserTo());
       String sql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
        jdbcTemplate.update(sql,transferDTO.getAmount(), accountFromId);

        sql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        jdbcTemplate.update(sql, transferDTO.getAmount(), accountToId);

        sql = "UPDATE transfers SET transfer_status_id = 2 WHERE transfer_id = ?";
        jdbcTemplate.update(sql,transferDTO.getTransferId());
        return "Transfer successful!";

    }

    @Override
    public String rejectRequest(TransferDTO transferDTO){
        long accountFromId = jdbcTemplate.queryForObject( "SELECT account_id FROM accounts WHERE user_id = ?", Long.class ,transferDTO.getUserFrom());
        long accountToId = jdbcTemplate.queryForObject( "SELECT account_id FROM accounts WHERE user_id = ?", Long.class ,transferDTO.getUserTo());
        String sql = "UPDATE transfers SET transfer_status_id = 3 WHERE transfer_id = ?";
        jdbcTemplate.update(sql, transferDTO.getTransferId());
        return "Request Rejected.";
    }



    @Override
    public List<TransferDTO> listTransfers(long userId){
        List<TransferDTO> transferList = new ArrayList<>();
        String sql = "SELECT transfer_id, account_from, account_to, amount, transfer_type_id, transfer_status_id FROM transfers WHERE account_from = (SELECT account_id FROM accounts WHERE user_id = ?) OR account_to = (SELECT account_id FROM accounts where user_id = ?)";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);

        while(results.next()){
            TransferDTO dto = mapRowToTransfer(results);
            transferList.add(dto);
        }
        return transferList;
    }

    @Override
    public List<TransferDTO> listRequests(long userId){
        List<TransferDTO> requestList = new ArrayList<>();
        String sql = "SELECT transfer_id, account_from, account_to, amount, transfer_type_id, transfer_status_id FROM transfers WHERE account_from = (SELECT account_id FROM accounts WHERE user_id = ?) AND transfer_status_id = 1";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()){
            TransferDTO dto = mapRowToTransfer(results);
            requestList.add(dto);

        }
        return requestList;
    }

    private TransferDTO mapRowToTransfer(SqlRowSet rowSet){
        TransferDTO dto = new TransferDTO();
        dto.setTransferId(rowSet.getLong("transfer_id"));
        dto.setAccountIdFrom(rowSet.getLong("account_from"));
        dto.setAccountIdTo(rowSet.getLong("account_to"));
        dto.setAmount(rowSet.getBigDecimal("amount"));
        dto.setTransferStatus(rowSet.getLong("transfer_status_id"));
        dto.setTransferType(rowSet.getLong("transfer_type_id"));
        dto.setUserFrom(jdbcTemplate.queryForObject("SELECT user_id FROM accounts WHERE account_id = ?",Long.class, dto.getAccountIdFrom()));
        dto.setUserTo(jdbcTemplate.queryForObject("SELECT user_id FROM accounts WHERE account_id = ?", Long.class, dto.getAccountIdTo()));
        dto.setUsernameFrom(jdbcTemplate.queryForObject("SELECT username FROM users WHERE user_id = ?", String.class, dto.getUserFrom()));
        dto.setUsernameTo(jdbcTemplate.queryForObject("SELECT username FROM users WHERE user_id = ?", String.class, dto.getUserTo()));
        dto.setTransferStatusDesc(jdbcTemplate.queryForObject("SELECT transfer_status_desc FROM transfer_statuses WHERE transfer_status_id = ?", String.class, dto.getTransferStatus()));
        dto.setTransferTypeDesc(jdbcTemplate.queryForObject("SELECT transfer_type_desc FROM transfer_types WHERE transfer_type_id = ?", String.class, dto.getTransferType()));

        return dto;

    }
}



















