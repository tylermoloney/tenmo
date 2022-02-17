package com.techelevator.tenmo.model;


import java.math.BigDecimal;

public class TransferDTO {

    private String usernameFrom;
    private String usernameTo;
    private long accountIdFrom;
    private long accountIdTo;
    private long transferId;
    private long userFrom;
    private long userTo;
    private BigDecimal amount;
    private long transferType;
    private long transferStatus;
    private String transferTypeDesc;
    private String transferStatusDesc;

    public long getTransferId() {
        return transferId;
    }

    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }

    public long getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(long userFrom) {
        this.userFrom = userFrom;
    }

    public long getUserTo() {
        return userTo;
    }

    public void setUserTo(long userTo) {
        this.userTo = userTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getUsernameFrom() {
        return usernameFrom;
    }

    public void setUsernameFrom(String usernameFrom) {
        this.usernameFrom = usernameFrom;
    }

    public String getUsernameTo() {
        return usernameTo;
    }

    public void setUsernameTo(String usernameTo) {
        this.usernameTo = usernameTo;
    }

    public long getAccountIdFrom() {
        return accountIdFrom;
    }

    public void setAccountIdFrom(long accountIdFrom) {
        this.accountIdFrom = accountIdFrom;
    }

    public long getAccountIdTo() {
        return accountIdTo;
    }

    public void setAccountIdTo(long accountIdTo) {
        this.accountIdTo = accountIdTo;
    }

    public long getTransferType() {
        return transferType;
    }

    public void setTransferType(long transferType) {
        this.transferType = transferType;
    }

    public long getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(long transferStatus) {
        this.transferStatus = transferStatus;
    }

    public String getTransferTypeDesc() {
        return transferTypeDesc;
    }

    public void setTransferTypeDesc(String transferTypeDesc) {
        this.transferTypeDesc = transferTypeDesc;
    }

    public String getTransferStatusDesc() {
        return transferStatusDesc;
    }

    public void setTransferStatusDesc(String transferStatusDesc) {
        this.transferStatusDesc = transferStatusDesc;
    }

    @Override
    public String toString() {
        return "Id:     " + transferId + "\n"
                +"From:   " + usernameFrom + "\n"
                +"To:     " + usernameTo + "\n"
                +"Type:   " + transferTypeDesc + "\n"
                +"Status: " + transferStatusDesc + "\n"
                +"Amount: " + amount;
    }
}
