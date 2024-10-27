package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.DTO.AssetBuyDTO;
import com.hackathon.bankingapp.DTO.AssetSellDTO;
import com.hackathon.bankingapp.DTO.TransactionResponseDTO;
import com.hackathon.bankingapp.Entities.Asset;
import com.hackathon.bankingapp.Entities.Transaction;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.ForbiddenException;
import com.hackathon.bankingapp.Exceptions.NotFoundException;
import com.hackathon.bankingapp.Exceptions.UnauthorizedException;
import com.hackathon.bankingapp.Repositories.TransactionRepository;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Repositories.AssetRepository;
import com.hackathon.bankingapp.Security.JwtTokenProvider;
import com.hackathon.bankingapp.Utils.Enums.TransactionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private JwtTokenProvider jwtProvider;
    @Autowired
    private AssetRepository assetRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void createPin(String pin, String password) {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found."));

        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new UnauthorizedException("Bad credentials");
        }

        user.setPin(pin);
        userRepository.save(user);
    }

    public void updatePin(String oldPin, String newPin, String password) {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found."));

        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new UnauthorizedException("Bad credentials");
        }

        if (!user.getPin().equals(oldPin)) {
            throw new UnauthorizedException("Invalid old pin");
        }

        user.setPin(newPin);
        userRepository.save(user);
    }

    public void deposit(String amount, String pin) {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found."));

        if (!user.getPin().equals(pin)) {
            throw new ForbiddenException("Invalid PIN");
        }

        user.setBalance(user.getBalance() + Double.parseDouble(amount));
        userRepository.save(user);
        Transaction transaction = new Transaction();
        transaction.setAmount(Double.parseDouble(amount));
        transaction.setTransactionType(TransactionType.CASH_DEPOSIT);
        transaction.setTransactionDate(System.currentTimeMillis());
        transaction.setSourceAccount(user);
        transactionRepository.save(transaction);
    }

    public void withdraw(String amount, String pin) {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found."));

        if (!user.getPin().equals(pin)) {
            throw new ForbiddenException("Invalid PIN");
        }

        if (Double.parseDouble(amount) > user.getBalance()) {
            throw new ForbiddenException("Insufficient balance");
        }

        user.setBalance(user.getBalance() - Double.parseDouble(amount));
        userRepository.save(user);
        Transaction transaction = new Transaction();
        transaction.setAmount(Double.parseDouble(amount));
        transaction.setTransactionType(TransactionType.CASH_WITHDRAWAL);
        transaction.setTransactionDate(System.currentTimeMillis());
        transaction.setSourceAccount(user);
        transactionRepository.save(transaction);
    }

    public void transfer(String amount, String pin, String targetAccountNumber) {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getPin().equals(pin)) {
            throw new ForbiddenException("Invalid PIN");
        }

        User targetAccount = userRepository.findByAccountNumber(UUID.fromString(targetAccountNumber))
                .orElseThrow(() -> new NotFoundException("Target account not found"));

        if (Double.parseDouble(amount) > user.getBalance()) {
            throw new ForbiddenException("Insufficient balance");
        }

        targetAccount.setBalance(targetAccount.getBalance() + Double.parseDouble(amount));
        user.setBalance(user.getBalance() - Double.parseDouble(amount));
        userRepository.save(user);
        userRepository.save(targetAccount);
        Transaction transaction = new Transaction();
        transaction.setAmount(Double.parseDouble(amount));
        transaction.setTransactionType(TransactionType.CASH_TRANSFER);
        transaction.setTransactionDate(System.currentTimeMillis());
        transaction.setSourceAccount(user);
        transaction.setTargetAccount(targetAccount);
        transactionRepository.save(transaction);
    }

    public List<TransactionResponseDTO> getTransactions() {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found"));
        return transactionRepository.findBySourceAccount(user).stream().map(transaction -> {
            TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();
            transactionResponseDTO.setId(transaction.getId());
            transactionResponseDTO.setAmount(transaction.getAmount());
            transactionResponseDTO.setTransactionType(transaction.getTransactionType().toString());
            transactionResponseDTO.setTransactionDate(transaction.getTransactionDate());
            transactionResponseDTO.setSourceAccountNumber(user.getAccountNumber().toString());
            transactionResponseDTO.setTargetAccountNumber(transaction.getTargetAccount() == null ? "N/A"
                    : transaction.getTargetAccount().getAccountNumber().toString());
            return transactionResponseDTO;
        }).toList();
    }

    public void buyAsset(AssetBuyDTO assetRequestDTO) {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getPin().equals(assetRequestDTO.getPin())) {
            throw new ForbiddenException("Invalid PIN");
        }

        Map<String, Double> assetPrices = getAssetPrices();
        if (!assetPrices.containsKey(assetRequestDTO.getAssetSymbol())) {
            throw new RuntimeException("Internal error occurred while purchasing the asset");
        }

        double currentPrice = assetPrices.get(assetRequestDTO.getAssetSymbol());
        double quantity = Math.round((Double.parseDouble(assetRequestDTO.getAmount()) / currentPrice) * 1e10) / 1e10;

        if (Double.parseDouble(assetRequestDTO.getAmount()) > user.getBalance()) {
            throw new RuntimeException("Internal error occurred while purchasing the asset");
        }

        Asset asset = new Asset();
        asset.setAssetSymbol(assetRequestDTO.getAssetSymbol());
        asset.setQuantity(quantity);
        asset.setPurchasePrice(currentPrice);
        asset.setPurchaseDate(System.currentTimeMillis());
        asset.setUser(user);
        assetRepository.save(asset);

        user.setBalance(user.getBalance() - Double.parseDouble(assetRequestDTO.getAmount()));
        userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setAmount(Double.parseDouble(assetRequestDTO.getAmount()));
        transaction.setTransactionType(TransactionType.ASSET_PURCHASE);
        transaction.setTransactionDate(System.currentTimeMillis());
        transaction.setSourceAccount(user);
        transactionRepository.save(transaction);

        double currentHoldings = getCurrentHoldings(user, assetRequestDTO.getAssetSymbol());
        double netWorth = calculateNetWorth();

        String emailBody = String.format("""
                Dear %s,

                You have successfully purchased %.2f units of %s for a total amount of $%.2f.

                Current holdings of %s: %.2f units

                Summary of current assets:
                %s

                Account Balance: $%.2f
                Net Worth: $%.2f

                Thank you for using our investment services.

                Best Regards,
                Investment Management Team
                """,
                user.getName(),
                quantity,
                assetRequestDTO.getAssetSymbol(),
                Double.parseDouble(assetRequestDTO.getAmount()),
                assetRequestDTO.getAssetSymbol(),
                currentHoldings,
                generateAssetSummary(user, assetRequestDTO.getAssetSymbol()),
                user.getBalance(),
                netWorth);

        emailService.sendEmail(
                user.getEmail(),
                "Investment Purchase Confirmation",
                emailBody);

        emailService.sendEmail(
                user.getEmail(),
                "Investment Purchase Confirmation",
                emailBody);
    }

    public void sellAsset(AssetSellDTO assetRequestDTO) {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.getPin().equals(assetRequestDTO.getPin())) {
            throw new ForbiddenException("Invalid PIN");
        }

        Map<String, Double> assetPrices = getAssetPrices();
        double currentPrice = assetPrices.get(assetRequestDTO.getAssetSymbol());
        double quantityToSell = Double.parseDouble(assetRequestDTO.getQuantity());

        List<Asset> userAssets = assetRepository.findByUserAndAssetSymbol(user, assetRequestDTO.getAssetSymbol());
        double totalHoldings = getCurrentHoldings(user, assetRequestDTO.getAssetSymbol());

        if (quantityToSell > totalHoldings) {
            throw new RuntimeException("Internal error occurred while purchasing the asset");
        }

        double totalGainLoss = 0;
        double remainingQuantityToSell = quantityToSell;

        for (Asset asset : userAssets) {
            if (remainingQuantityToSell <= 0)
                break;

            double quantityFromThisAsset = Math.min(remainingQuantityToSell, asset.getQuantity());
            double gainLoss = (currentPrice - asset.getPurchasePrice()) * quantityFromThisAsset;
            totalGainLoss += gainLoss;

            if (quantityFromThisAsset == asset.getQuantity()) {
                assetRepository.delete(asset);
            } else {
                asset.setQuantity(asset.getQuantity() - quantityFromThisAsset);
                assetRepository.save(asset);
            }

            remainingQuantityToSell -= quantityFromThisAsset;
        }

        double saleProceeds = quantityToSell * currentPrice;
        user.setBalance(user.getBalance() + saleProceeds);
        userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setAmount(saleProceeds);
        transaction.setTransactionType(TransactionType.ASSET_SELL);
        transaction.setTransactionDate(System.currentTimeMillis());
        transaction.setSourceAccount(user);
        transactionRepository.save(transaction);

        double remainingHoldings = getCurrentHoldings(user, assetRequestDTO.getAssetSymbol());
        double netWorth = calculateNetWorth();

        String emailBody = String.format("""
                Dear %s,

                You have successfully sold %.2f units of %s.

                Total Gain/Loss: $%.2f

                Remaining holdings of %s: %.2f units

                Summary of current assets:
                %s

                Account Balance: $%.2f
                Net Worth: $%.2f

                Thank you for using our investment services.

                Best Regards,
                Investment Management Team
                """,
                user.getName(),
                quantityToSell,
                assetRequestDTO.getAssetSymbol(),
                totalGainLoss,
                assetRequestDTO.getAssetSymbol(),
                remainingHoldings,
                generateAssetSummary(user, assetRequestDTO.getAssetSymbol()),
                user.getBalance(),
                netWorth);

        emailService.sendEmail(
                user.getEmail(),
                "Investment Sale Confirmation",
                emailBody);
    }

    public Map<String, Double> getUserAssets() {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Map<String, Double> holdings = assetRepository.findByUser(user).stream()
                .collect(Collectors.groupingBy(
                        Asset::getAssetSymbol,
                        Collectors.summingDouble(Asset::getQuantity)));

        return holdings;
    }

    public List<Asset> getUserAssetHistory(User user, String symbol) {
        return assetRepository.findByUserAndAssetSymbol(user, symbol);
    }

    public double calculateAveragePurchasePrice(List<Asset> assets) {
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

    public Map<String, Double> getAssetPrices() {
        ResponseEntity<Map<String, Double>> response = restTemplate.exchange(
                "https://faas-lon1-917a94a7.doserverless.co/api/v1/web/fn-e0f31110-7521-4cb9-86a2-645f66eefb63/default/market-prices-simulator",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Double>>() {
                });

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Internal error occurred while purchasing the asset");
        }

        return response.getBody();
    }

    private double getCurrentHoldings(User user, String assetSymbol) {
        return assetRepository.findByUserAndAssetSymbol(user, assetSymbol)
                .stream()
                .mapToDouble(Asset::getQuantity)
                .sum();
    }

    private String generateAssetSummary(User user, String symbol) {
        StringBuilder summary = new StringBuilder();
        List<Asset> assets = assetRepository.findByUserAndAssetSymbol(user, symbol);
        
        Map<Double, Double> assetsByPrice = assets.stream()
            .collect(Collectors.groupingBy(
                Asset::getPurchasePrice,
                Collectors.summingDouble(Asset::getQuantity)
            ));
        
        assetsByPrice.forEach((price, quantity) -> {
            summary.append(String.format("- %s: %.2f units purchased at $%.2f%n", 
                symbol, quantity, price));
        });
        
        return summary.toString();
    }

    public double calculateNetWorth() {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Map<String, Double> currentPrices = getAssetPrices();
        double assetsValue = assetRepository.findByUser(user).stream()
                .mapToDouble(asset -> asset.getQuantity() * currentPrices.get(asset.getAssetSymbol()))
                .sum();
        return user.getBalance() + assetsValue;
    }
}
