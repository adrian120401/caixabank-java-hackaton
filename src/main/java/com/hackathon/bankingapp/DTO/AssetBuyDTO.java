package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssetBuyDTO {
    
    @NotBlank
    private String assetSymbol;

    @NotBlank
    private String amount;

    @NotBlank
    private String pin;
}
