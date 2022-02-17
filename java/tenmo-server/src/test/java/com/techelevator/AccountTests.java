package com.techelevator;


import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;

public class AccountTests {

    public static SingleConnectionDataSource ds;
    private UserDao userDao;
    private JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;

    @BeforeClass
    public static void runFirst(){
        ds = new SingleConnectionDataSource();
        ds.setUrl("jdbc:postgresql://localhost:5432/tenmo");
        ds.setUsername("postgres");
        ds.setPassword("postgres1");
        ds.setAutoCommit(false);
    }
    @AfterClass
    public static void runlast(){
        ds.destroy();
    }


    private Account testAccount;
    @Before
    public void beforeEach(){
        jdbcTemplate = new JdbcTemplate(ds);
        userDao = new JdbcUserDao(jdbcTemplate);
        accountDao = new JdbcAccountDao(jdbcTemplate);
        String sql = "INSERT INTO users (user_id, username, password_hash) VALUES (5001, 'testuser', 'aaaaaaaaaaaa')";
        jdbcTemplate.update(sql);
        sql = "INSERT INTO accounts (account_id, user_id, balance) VALUES (6001, 5001, 5)";
        jdbcTemplate.update(sql);
        testAccount = new Account();
        testAccount.setAccountId(6001L);
        testAccount.setUserId(5001L);
        testAccount.setAccountBalance(new BigDecimal("5.00"));
    }

    @After
    public void afterEach() throws SQLException {
        ds.getConnection().rollback();
    }

    @Test
    public void getUsernameFromAccountId_should_return_expected_username() {


        String expected = "testuser";
        String actual = userDao.getUsernameFromAccountId(6001);
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void getBalance_should_return_expected_balance(){
        BigDecimal expected = new BigDecimal("5.00");
        BigDecimal actual = accountDao.getBalance(6001L);
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void setBalance_properly_updates_balance(){
        BigDecimal expected = new BigDecimal("10.00");
        accountDao.setBalance(6001L,new BigDecimal("10.00"));
        BigDecimal actual = accountDao.getBalance(6001L);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getAccountById_returns_correct_account(){
        Account account = accountDao.getAccountById(6001L);
        assertAccountsMatch("getAccountById returned wrong account", testAccount, account);
    }

    @Test
    public void getAccountByUserId_returns_correct_account(){
        Account account = accountDao.getAccountByUserId(5001L);
        assertAccountsMatch("getAccountById returned wrong account", testAccount, account);
    }

    private void assertAccountsMatch(String message, Account expected, Account actual){
        Assert.assertEquals(message, expected.getAccountBalance(), actual.getAccountBalance());
        Assert.assertEquals(message, expected.getAccountId(), actual.getAccountId());
        Assert.assertEquals(message, expected.getUserId(), actual.getUserId());
    }
}



