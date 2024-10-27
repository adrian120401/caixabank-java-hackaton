package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.DTO.TransactionResponseDTO;
import com.hackathon.bankingapp.Entities.Transaction;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.ForbiddenException;
import com.hackathon.bankingapp.Exceptions.NotFoundException;
import com.hackathon.bankingapp.Exceptions.UnauthorizedException;
import com.hackathon.bankingapp.Repositories.TransactionRepository;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Security.JwtTokenProvider;
import com.hackathon.bankingapp.Utils.Enums.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private JwtTokenProvider jwtProvider;

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

        User targetAccount = userRepository.findByAccountNumber(UUID.fromString(targetAccountNumber)).orElseThrow(() -> new NotFoundException("Target account not found"));

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
            transactionResponseDTO.setTargetAccountNumber(transaction.getTargetAccount() == null ? "N/A" : transaction.getTargetAccount().getAccountNumber().toString());
            return transactionResponseDTO;
        }).toList();
    }
}
