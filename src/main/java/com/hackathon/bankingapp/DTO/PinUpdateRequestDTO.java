package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PinUpdateRequestDTO {
    
    @NotBlank(message = "Old pin is required")
    private String oldPin;

    @NotBlank(message = "New pin is required")
    private String newPin;

    @NotBlank(message = "Password is required")
    private String password;
}
