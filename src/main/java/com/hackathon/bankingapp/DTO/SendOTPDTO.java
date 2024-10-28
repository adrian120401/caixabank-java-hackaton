package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendOTPDTO {
    @NotBlank(message = "Identifier is required")
    private String identifier;
}
