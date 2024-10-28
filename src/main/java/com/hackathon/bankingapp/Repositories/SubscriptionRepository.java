package com.hackathon.bankingapp.Repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hackathon.bankingapp.Entities.Subscription;
import com.hackathon.bankingapp.Entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    List<Subscription> findByActive(boolean active);
    Optional<Subscription> findByUser(User user);
}
