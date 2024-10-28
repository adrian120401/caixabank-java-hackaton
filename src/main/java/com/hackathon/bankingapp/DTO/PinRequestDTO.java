package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PinRequestDTO {
    
    @NotBlank(message = "Pin is required")
    private String pin;

    @NotBlank(message = "Password is required")
    private String password;
}
