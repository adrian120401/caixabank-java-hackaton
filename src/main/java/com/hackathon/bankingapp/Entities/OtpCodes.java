package com.hackathon.bankingapp.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "otp_codes")
public class OtpCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
