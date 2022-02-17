package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

        public Account getAccountById(Long accountId);

        BigDecimal getBalance(Long accountId);

        void setBalance(Long accountId, BigDecimal balance);

        Account getAccountByUserId(Long accountId);
}
