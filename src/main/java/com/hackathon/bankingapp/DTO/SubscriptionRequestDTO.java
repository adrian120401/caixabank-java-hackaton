package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionRequestDTO {
    
    @NotNull
    @Min(0) 
    private double amount;

    @NotNull 
    @Min(1)
    private int intervalSeconds;

    @NotBlank
    private String pin;
}
