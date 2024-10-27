package com.hackathon.bankingapp.DTO;

import lombok.Data;

@Data
public class UserResponseDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String accountNumber;
    private String hashedPassword;

    public UserResponseDTO(String name, String email, String phoneNumber, String address, String accountNumber, String hashedPassword) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.accountNumber = accountNumber;
        this.hashedPassword = hashedPassword;
    }
}
