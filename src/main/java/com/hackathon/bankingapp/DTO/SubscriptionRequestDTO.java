package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionRequestDTO {
    
    @NotBlank
    private String amount;

    @NotNull
    private int intervalSeconds;

    @NotBlank
    private String pin;
}
