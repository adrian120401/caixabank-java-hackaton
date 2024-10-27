package com.hackathon.bankingapp.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Column;
import lombok.Data;

@Data
@Entity
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String assetSymbol;

    @Column(nullable = false)
    private double quantity;

    @Column(nullable = false)
    private double purchasePrice;

    @Column(nullable = false)
    private long purchaseDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
