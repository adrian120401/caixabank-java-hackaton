package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TradingBotRequestDTO {
    
    @NotBlank(message = "PIN cannot be null or empty")
    @NotNull(message = "PIN cannot be null or empty")
    private String pin;
}
