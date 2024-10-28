package com.hackathon.bankingapp.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackathon.bankingapp.Repositories.TransactionRepository;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Security.JwtTokenProvider;
import com.hackathon.bankingapp.Utils.Enums.TransactionType;

import lombok.extern.slf4j.Slf4j;

import com.hackathon.bankingapp.DTO.SubscriptionRequestDTO;
import com.hackathon.bankingapp.Entities.Transaction;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.ForbiddenException;
import com.hackathon.bankingapp.Exceptions.NotFoundException;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SubscriptionService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtProvider;
    @Autowired
    private TransactionRepository transactionRepository;
    
    private final Map<String, ScheduledFuture<?>> activeSubscriptions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void createSubscription(SubscriptionRequestDTO subscriptionRequestDTO) {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getPin().equals(subscriptionRequestDTO.getPin())) {
            throw new ForbiddenException("Invalid PIN");
        }

        String userId = user.getEmail();
        cancelExistingSubscription(userId);

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            try {
                processSubscriptionPayment(user, subscriptionRequestDTO.getAmount());
            } catch (Exception e) {
                log.error("Error processing subscription payment: {}", e.getMessage());
                cancelExistingSubscription(userId);
            }
        }, 0, subscriptionRequestDTO.getIntervalSeconds(), TimeUnit.SECONDS);

        activeSubscriptions.put(userId, future);
    }

    private void processSubscriptionPayment(User user, double amount) {
        if (user.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }
        
        user.setBalance(user.getBalance() - amount);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.SUBSCRIPTION);
        transaction.setTransactionDate(System.currentTimeMillis());
        transaction.setSourceAccount(user);
        
        transactionRepository.save(transaction);
    }

    private void cancelExistingSubscription(String userId) {
        ScheduledFuture<?> existingFuture = activeSubscriptions.get(userId);
        if (existingFuture != null) {
            existingFuture.cancel(false);
            activeSubscriptions.remove(userId);
        }
    }
}
