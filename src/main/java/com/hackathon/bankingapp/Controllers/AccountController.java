package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.AssetBuyDTO;
import com.hackathon.bankingapp.DTO.AssetSellDTO;
import com.hackathon.bankingapp.DTO.TransactionResponseDTO;
import com.hackathon.bankingapp.Services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/pin/create")
    public ResponseEntity<Map<String, String>> createPint(@RequestBody Map<String, Object> requestBody) {
        String pin = (String) requestBody.get("pin");
        String password = (String) requestBody.get("password");

        if (pin == null || pin.isEmpty() || password == null || password.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Pin and password are required");
            return ResponseEntity.badRequest().body(response);
        }
        accountService.createPin(pin, password);

        Map<String, String> response = new HashMap<>();
        response.put("msg", "PIN created successfully");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/pin/update")
    public ResponseEntity<Map<String, String>> updatePint(@RequestBody Map<String, Object> requestBody) {
        String oldPin = (String) requestBody.get("oldPin");
        String newPin = (String) requestBody.get("newPin");
        String password = (String) requestBody.get("password");

        if (oldPin == null || oldPin.isEmpty() || newPin == null || newPin.isEmpty() || password == null
                || password.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Pin and password are required");
            return ResponseEntity.badRequest().body(response);
        }
        accountService.updatePin(oldPin, newPin, password);

        Map<String, String> response = new HashMap<>();
        response.put("msg", "PIN updated successfully");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, String>> deposit(@RequestBody Map<String, Object> requestBody) {
        Object amountObj = requestBody.get("amount");
        String amount = amountObj != null ? amountObj.toString() : null;
        String pin = (String) requestBody.get("pin");

        if (amount == null || amount.isEmpty() || pin == null || pin.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Amount and pin are required");
            return ResponseEntity.badRequest().body(response);
        }
        accountService.deposit(amount, pin);

        Map<String, String> response = new HashMap<>();
        response.put("msg", "Cash deposited successfully");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, String>> withdraw(@RequestBody Map<String, Object> requestBody) {
        Object amountObj = requestBody.get("amount");
        String amount = amountObj != null ? amountObj.toString() : null;
        String pin = (String) requestBody.get("pin");

        if (amount == null || amount.isEmpty() || pin == null || pin.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Amount and pin are required");
            return ResponseEntity.badRequest().body(response);
        }
        accountService.withdraw(amount, pin);

        Map<String, String> response = new HashMap<>();
        response.put("msg", "Cash withdrawn successfully");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> transfer(@RequestBody Map<String, Object> requestBody) {
        Object amountObj = requestBody.get("amount");
        String amount = amountObj != null ? amountObj.toString() : null;
        String pin = (String) requestBody.get("pin");
        String targetAccountNumber = (String) requestBody.get("targetAccountNumber");

        if (amount == null || amount.isEmpty() || pin == null || pin.isEmpty() || targetAccountNumber == null
                || targetAccountNumber.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Amount, pin and targetAccountNumber are required");
            return ResponseEntity.badRequest().body(response);
        }
        accountService.transfer(amount, pin, targetAccountNumber);

        Map<String, String> response = new HashMap<>();
        response.put("msg", "Fund transferred successfully");
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions() {
        List<TransactionResponseDTO> transactions = accountService.getTransactions();
        return ResponseEntity.ok().body(transactions);
    }

    @PostMapping("/buy-asset")
    public ResponseEntity<String> buyAsset(@RequestBody AssetBuyDTO assetRequestDTO) {
        accountService.buyAsset(assetRequestDTO);
        return ResponseEntity.ok("Asset purchase successful.");
    }

    @PostMapping("/sell-asset")
    public ResponseEntity<String> sellAsset(@RequestBody AssetSellDTO assetRequestDTO) {
        accountService.sellAsset(assetRequestDTO);
        return ResponseEntity.ok("Asset sale successful.");
    }

    @GetMapping("/net-worth")
    public ResponseEntity<Double> getNetWorth() {
        return ResponseEntity.ok(accountService.calculateNetWorth());
    }

    @GetMapping("/assets")
    public ResponseEntity<Map<String, Double>> getUserAssets() {
        Map<String, Double> holdings = accountService.getUserAssets();
        return ResponseEntity.ok(holdings);
    }
}
