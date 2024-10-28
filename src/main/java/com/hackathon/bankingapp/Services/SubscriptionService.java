package com.hackathon.bankingapp.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hackathon.bankingapp.Repositories.SubscriptionRepository;
import com.hackathon.bankingapp.Repositories.TransactionRepository;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Security.JwtTokenProvider;
import com.hackathon.bankingapp.Utils.Enums.TransactionType;
import com.hackathon.bankingapp.DTO.SubscriptionRequestDTO;
import com.hackathon.bankingapp.Entities.Subscription;
import com.hackathon.bankingapp.Entities.Transaction;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.ForbiddenException;
import com.hackathon.bankingapp.Exceptions.NotFoundException;
import com.hackathon.bankingapp.Exceptions.BadRequestException;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private JwtTokenProvider jwtProvider;

    public void createSubscription(SubscriptionRequestDTO subscriptionRequestDTO) {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getPin().equals(subscriptionRequestDTO.getPin())) {
            throw new ForbiddenException("Invalid PIN");
        }

        if (user.getBalance() < subscriptionRequestDTO.getAmount()) {
            throw new BadRequestException("Insufficient balance");
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setAmount(subscriptionRequestDTO.getAmount());
        subscription.setIntervalSeconds(subscriptionRequestDTO.getIntervalSeconds());
        subscription.setActive(true);
        subscription.setLastExecutionTime(System.currentTimeMillis());

        subscriptionRepository.save(subscription);
    }

    @Scheduled(fixedRate = 1000)
    public void processSubscriptions() {
        List<Subscription> activeSubscriptions = subscriptionRepository.findByActive(true);
        long currentTime = System.currentTimeMillis();

        for (Subscription sub : activeSubscriptions) {
            if ((currentTime - sub.getLastExecutionTime()) >= (sub.getIntervalSeconds() * 1000L)) {
                User user = sub.getUser();

                if (user.getBalance() < sub.getAmount()) {
                    sub.setActive(false);
                    subscriptionRepository.save(sub);
                    continue;
                }

                try {
                    user.setBalance(user.getBalance() - sub.getAmount());
                    userRepository.save(user);

                    Transaction transaction = new Transaction();
                    transaction.setAmount(sub.getAmount());
                    transaction.setTransactionType(TransactionType.SUBSCRIPTION);
                    transaction.setTransactionDate(currentTime);
                    transaction.setSourceAccount(user);
                    transactionRepository.save(transaction);

                    sub.setLastExecutionTime(currentTime);
                    subscriptionRepository.save(sub);
                } catch (Exception e) {
                    sub.setActive(false);
                    subscriptionRepository.save(sub);
                }
            }
        }
    }
}
