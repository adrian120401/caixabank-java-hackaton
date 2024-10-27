package com.hackathon.bankingapp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackathon.bankingapp.Entities.TradingBot;

import java.util.List;
import java.util.UUID;

@Repository
public interface TradingBotRepository extends JpaRepository<TradingBot, UUID> {
    List<TradingBot> findByActive(boolean active);
}
