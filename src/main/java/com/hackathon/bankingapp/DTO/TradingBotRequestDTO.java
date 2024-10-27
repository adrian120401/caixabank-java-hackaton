package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TradingBotRequestDTO {
    
    @NotBlank
    private String pin;
}
