package com.hackathon.bankingapp.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hackathon.bankingapp.DTO.TradingBotRequestDTO;
import com.hackathon.bankingapp.Entities.Asset;
import com.hackathon.bankingapp.Entities.TradingBot;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.ForbiddenException;
import com.hackathon.bankingapp.Exceptions.NotFoundException;
import com.hackathon.bankingapp.Repositories.TradingBotRepository;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Security.JwtTokenProvider;
import com.hackathon.bankingapp.Utils.BotConst;

import java.util.List;
import java.util.Map;

@Service
public class TradingBotService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TradingBotRepository tradingBotRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private JwtTokenProvider jwtProvider;

    public void createTradingBot(TradingBotRequestDTO tradingBotRequestDTO) {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getPin().equals(tradingBotRequestDTO.getPin())) {
            throw new ForbiddenException("Invalid PIN");
        }

        TradingBot tradingBot = new TradingBot();
        tradingBot.setUser(user);
        tradingBot.setActive(true);
        tradingBotRepository.save(tradingBot);
    }

    @Scheduled(fixedRate = 30000)
    public void processTradingBots() {
        List<TradingBot> activeBots = tradingBotRepository.findByActive(true);
        Map<String, Double> currentPrices = accountService.getAssetPrices();

        for (TradingBot bot : activeBots) {
            Map<String, Double> userAssets = accountService.getUserAssets();
            for (Map.Entry<String, Double> asset : userAssets.entrySet()) {
                List<Asset> assetHistory = accountService.getUserAssetHistory(bot.getUser(), asset.getKey());
                double avgPurchasePrice = accountService.calculateAveragePurchasePrice(assetHistory);
                double currentPrice = currentPrices.get(asset.getKey());

                if (currentPrice <= avgPurchasePrice * BotConst.BUY_THRESHOLD) {
                    accountService.tryToBuy(bot.getUser(), asset.getKey(), 100.0);
                } else if (currentPrice >= avgPurchasePrice * BotConst.SELL_THRESHOLD) {
                    accountService.tryToSell(bot.getUser(), asset.getKey(), asset.getValue() * 0.1);
                }
            }
        }
    }
}
