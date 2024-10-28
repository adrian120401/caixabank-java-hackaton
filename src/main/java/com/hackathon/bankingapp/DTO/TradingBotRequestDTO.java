package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TradingBotRequestDTO {
    
    @NotBlank(message = "PIN cannot be null or empty")
    private String pin;
}
