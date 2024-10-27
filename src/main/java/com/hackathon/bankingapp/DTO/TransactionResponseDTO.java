package com.hackathon.bankingapp.DTO;

import lombok.Data;

@Data
public class TransactionResponseDTO {
    private Long id;
    private double amount;
    private String transactionType;
    private Long transactionDate;
    private String sourceAccountNumber;
    private String targetAccountNumber;
}
