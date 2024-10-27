package com.hackathon.bankingapp.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.bankingapp.DTO.SubscriptionRequestDTO;
import com.hackathon.bankingapp.DTO.TradingBotRequestDTO;
import com.hackathon.bankingapp.Services.SubscriptionService;
import com.hackathon.bankingapp.Services.TradingBotService;

@RestController
@RequestMapping("/api/user-actions")
public class UserActionsController {

    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private TradingBotService tradingBotService;

    @PostMapping("/subscribe")
    public ResponseEntity<String> createSubscription(@RequestBody SubscriptionRequestDTO subscriptionRequestDTO) {
        subscriptionService.createSubscription(subscriptionRequestDTO);
        return ResponseEntity.ok("Subscription created successfully.");
    }

    @PostMapping("/enable-auto-invest")
    public ResponseEntity<String> enableAutoInvest(@RequestBody TradingBotRequestDTO tradingBotRequestDTO) {
        tradingBotService.createTradingBot(tradingBotRequestDTO);
        return ResponseEntity.ok("Automatic investment enabled successfully.");
    }
}
