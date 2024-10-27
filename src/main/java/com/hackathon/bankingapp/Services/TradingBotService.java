package com.hackathon.bankingapp.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hackathon.bankingapp.DTO.TradingBotRequestDTO;
import com.hackathon.bankingapp.Entities.Asset;
import com.hackathon.bankingapp.Entities.TradingBot;
import com.hackathon.bankingapp.Entities.Transaction;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.ForbiddenException;
import com.hackathon.bankingapp.Exceptions.NotFoundException;
import com.hackathon.bankingapp.Repositories.TradingBotRepository;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Repositories.AssetRepository;
import com.hackathon.bankingapp.Repositories.TransactionRepository;
import com.hackathon.bankingapp.Security.JwtTokenProvider;
import com.hackathon.bankingapp.Utils.BotConst;
import com.hackathon.bankingapp.Utils.Enums.TransactionType;

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
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public void createTradingBot(TradingBotRequestDTO tradingBotRequestDTO) {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getPin().equals(tradingBotRequestDTO.getPin())) {
            throw new ForbiddenException("Invalid PIN");
        }

        TradingBot bot = new TradingBot();
        bot.setUser(user);
        bot.setActive(true);
        bot.setLastCheckTime(System.currentTimeMillis());
        tradingBotRepository.save(bot);
    }

    @Scheduled(fixedRate = 30000)
    public void processTradingBots() {
        List<TradingBot> activeBots = tradingBotRepository.findByActive(true);
        Map<String, Double> currentPrices = accountService.getAssetPrices();

        for (TradingBot bot : activeBots) {
            User user = bot.getUser();
            List<Asset> userAssets = assetRepository.findByUser(user);

            for (Asset asset : userAssets) {
                double avgPurchasePrice = calculateAveragePurchasePrice(
                        assetRepository.findByUserAndAssetSymbol(user, asset.getAssetSymbol()));
                double currentPrice = currentPrices.get(asset.getAssetSymbol());

                if (currentPrice <= avgPurchasePrice * BotConst.BUY_THRESHOLD) {
                    double amountToInvest = 100.0;
                    if (user.getBalance() >= amountToInvest) {
                        double quantity = Math.round((amountToInvest / currentPrice) * 1e10) / 1e10;

                        Asset newAsset = new Asset();
                        newAsset.setAssetSymbol(asset.getAssetSymbol());
                        newAsset.setQuantity(quantity);
                        newAsset.setPurchasePrice(currentPrice);
                        newAsset.setPurchaseDate(System.currentTimeMillis());
                        newAsset.setUser(user);
                        assetRepository.save(newAsset);

                        user.setBalance(user.getBalance() - amountToInvest);
                        userRepository.save(user);

                        saveTransaction(user, amountToInvest, TransactionType.ASSET_PURCHASE);
                    }
                } else if (currentPrice >= avgPurchasePrice * BotConst.SELL_THRESHOLD) {
                    double quantityToSell = asset.getQuantity() * 0.1;
                    double saleAmount = quantityToSell * currentPrice;

                    asset.setQuantity(asset.getQuantity() - quantityToSell);
                    if (asset.getQuantity() > 0) {
                        assetRepository.save(asset);
                    } else {
                        assetRepository.delete(asset);
                    }

                    user.setBalance(user.getBalance() + saleAmount);
                    userRepository.save(user);

                    saveTransaction(user, saleAmount, TransactionType.ASSET_SELL);
                }
            }
        }
    }

    private double calculateAveragePurchasePrice(List<Asset> assets) {
        if (assets.isEmpty())
            return 0;

        double totalCost = assets.stream()
                .mapToDouble(asset -> asset.getQuantity() * asset.getPurchasePrice())
                .sum();

        double totalQuantity = assets.stream()
                .mapToDouble(Asset::getQuantity)
                .sum();

        return totalCost / totalQuantity;
    }

    private void saveTransaction(User user, double amount, TransactionType type) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(type);
        transaction.setTransactionDate(System.currentTimeMillis());
        transaction.setSourceAccount(user);
        transactionRepository.save(transaction);
    }
}
