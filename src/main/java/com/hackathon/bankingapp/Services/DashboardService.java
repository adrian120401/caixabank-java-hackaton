package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.DTO.AccountInfoResponse;
import com.hackathon.bankingapp.DTO.UserResponseDTO;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.NotFoundException;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DashboardService {
    private static final Logger logger = LoggerFactory.getLogger(DashboardService.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtProvider;

    public UserResponseDTO getUser() {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user =  userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found."));
        return new UserResponseDTO(user.getName(), user.getEmail(), user.getPhoneNumber(), user.getAddress(), user.getAccountNumber(), user.getHashedPassword());
    }

    public AccountInfoResponse getAccount() {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user =  userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found."));
        return new AccountInfoResponse(user.getAccountNumber().toString(), 0.0);
    }
}
