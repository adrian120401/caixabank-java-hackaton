package com.hackathon.bankingapp.Entities;

import com.hackathon.bankingapp.Utils.Enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Long transactionDate;

    @ManyToOne
    @JoinColumn(name = "source_account_number", referencedColumnName = "accountNumber", nullable = false)
    private User sourceAccount;

    @ManyToOne
    @JoinColumn(name = "target_account_number", referencedColumnName = "accountNumber", nullable = true)
    private User targetAccount;
}
