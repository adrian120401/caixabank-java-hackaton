package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransferRequestDTO {

    @NotBlank
    private String amount;

    @NotBlank
    private String pin;

    @NotBlank
    private String targetAccountNumber;
}
