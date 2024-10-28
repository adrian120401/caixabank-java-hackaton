package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.AssetBuyDTO;
import com.hackathon.bankingapp.DTO.AssetSellDTO;
import com.hackathon.bankingapp.DTO.DepositRequestDTO;
import com.hackathon.bankingapp.DTO.PinRequestDTO;
import com.hackathon.bankingapp.DTO.PinUpdateRequestDTO;
import com.hackathon.bankingapp.DTO.TransactionResponseDTO;
import com.hackathon.bankingapp.DTO.TransferRequestDTO;
import com.hackathon.bankingapp.DTO.WithdrawRequestDTO;
import com.hackathon.bankingapp.Services.AccountService;

import jakarta.validation.Valid;

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
    public ResponseEntity<Map<String, String>> createPint(@RequestBody @Valid PinRequestDTO pinRequestDTO) {
        accountService.createPin(pinRequestDTO.getPin(), pinRequestDTO.getPassword());

        Map<String, String> response = new HashMap<>();
        response.put("msg", "PIN created successfully");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/pin/update")
    public ResponseEntity<Map<String, String>> updatePint(@RequestBody @Valid PinUpdateRequestDTO pinUpdateRequestDTO) {
        accountService.updatePin(pinUpdateRequestDTO.getOldPin(), pinUpdateRequestDTO.getNewPin(),
                pinUpdateRequestDTO.getPassword());

        Map<String, String> response = new HashMap<>();
        response.put("msg", "PIN updated successfully");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, String>> deposit(@RequestBody @Valid DepositRequestDTO request) {
        accountService.deposit(request.getAmount(), request.getPin());
        return ResponseEntity.ok(Map.of("msg", "Cash deposited successfully"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, String>> withdraw(@RequestBody @Valid WithdrawRequestDTO request) {
        accountService.withdraw(request.getAmount(), request.getPin());
        return ResponseEntity.ok(Map.of("msg", "Cash withdrawn successfully"));
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<Map<String, String>> transfer(@RequestBody @Valid TransferRequestDTO request) {
        accountService.transfer(request.getAmount(), request.getPin(), request.getTargetAccountNumber());
        return ResponseEntity.ok(Map.of("msg", "Fund transferred successfully"));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactions() {
        return ResponseEntity.ok(accountService.getTransactions());
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
