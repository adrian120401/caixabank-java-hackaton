package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOTPDTO {

    @NotBlank(message = "Identifier is required")
    private String identifier;
    
    @NotBlank(message = "OTP is required")
    private String otp;
}
