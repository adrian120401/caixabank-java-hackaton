package com.hackathon.bankingapp.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.bankingapp.Services.AccountService;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/market")
public class MarketController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/prices")
    public ResponseEntity<Map<String, Double>> getAllPrices() {
        return ResponseEntity.ok(accountService.getAssetPrices());
    }

    @GetMapping("/prices/{symbol}")
    public ResponseEntity<Double> getAssetPrice(@PathVariable String symbol) {
        Map<String, Double> prices = accountService.getAssetPrices();
        return ResponseEntity.ok(prices.get(symbol));
    }
}
