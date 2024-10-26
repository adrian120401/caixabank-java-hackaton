package com.hackathon.bankingapp.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponseDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private UUID accountNumber;
    private String hashedPassword;

    public UserResponseDTO(String name, String email, String phoneNumber, String address, UUID accountNumber, String hashedPassword) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.accountNumber = accountNumber;
        this.hashedPassword = hashedPassword;
    }
}
