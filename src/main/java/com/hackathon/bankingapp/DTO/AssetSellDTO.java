package com.hackathon.bankingapp.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssetSellDTO {
    @NotBlank
    private String assetSymbol;

    @NotBlank
    private String quantity;

    @NotBlank
    private String pin;
}
