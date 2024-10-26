package com.hackathon.bankingapp.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    @Column(unique = true)
    private String email;

    private String address;

    @Column(unique = true)
    private UUID accountNumber;

    @Column(unique = true)
    private String phoneNumber;

    private String hashedPassword;

    @PrePersist
    public void generateAccountNumber() {
        this.accountNumber = UUID.randomUUID();
    }
}
