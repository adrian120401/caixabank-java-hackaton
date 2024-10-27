package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubscriptionRequestDTO {
    
    @NotBlank
    private double amount;

    @NotBlank
    private int intervalSeconds;

    @NotBlank
    private String pin;
}
