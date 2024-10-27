package com.hackathon.bankingapp.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hackathon.bankingapp.Repositories.SubscriptionRepository;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Security.JwtTokenProvider;
import com.hackathon.bankingapp.DTO.SubscriptionRequestDTO;
import com.hackathon.bankingapp.Entities.Subscription;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.ForbiddenException;
import com.hackathon.bankingapp.Exceptions.NotFoundException;

import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private JwtTokenProvider jwtProvider;

    public void createSubscription(SubscriptionRequestDTO subscriptionRequestDTO) {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getPin().equals(subscriptionRequestDTO.getPin())) {
            throw new ForbiddenException("Invalid PIN");
        }

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setAmount(subscriptionRequestDTO.getAmount());
        subscription.setIntervalSeconds(subscriptionRequestDTO.getIntervalSeconds());
        subscription.setActive(true);
        subscription.setLastExecutionTime(System.currentTimeMillis() / 1000);

        subscriptionRepository.save(subscription);
    }

    @Scheduled(fixedRate = 5000)
    public void processSubscriptions() {
        List<Subscription> activeSubscriptions = subscriptionRepository.findByActive(true);
        for (Subscription sub : activeSubscriptions) {
            long currentTime = System.currentTimeMillis() / 1000;
            if (currentTime - sub.getLastExecutionTime() >= sub.getIntervalSeconds()) {
                try {
                    accountService.withdraw(String.valueOf(sub.getAmount()), sub.getUser().getPin());
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
