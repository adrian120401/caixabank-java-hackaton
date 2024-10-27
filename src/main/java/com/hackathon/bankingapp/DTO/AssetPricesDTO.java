package com.hackathon.bankingapp.DTO;

import java.util.Map;

import lombok.Data;

@Data
public class AssetPricesDTO {
     private Map<String, Double> prices;

    public AssetPricesDTO(Map<String, Double> prices) {
        this.prices = prices;
    }
}
