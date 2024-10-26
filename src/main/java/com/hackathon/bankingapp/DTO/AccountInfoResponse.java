package com.hackathon.bankingapp.DTO;

import lombok.Data;

@Data
public class AccountInfoResponse {
    private String accountNumber;
    private double balance;

    public AccountInfoResponse(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }
}
