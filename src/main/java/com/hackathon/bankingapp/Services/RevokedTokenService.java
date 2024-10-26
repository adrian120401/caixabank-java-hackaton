package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.Entities.RevokedToken;
import com.hackathon.bankingapp.Repositories.RevokedTokenRepository;
import com.hackathon.bankingapp.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RevokedTokenService {

    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    @Autowired
    private JwtTokenProvider jwtProvider;

    public void revokeToken() {
        String token = jwtProvider.getTokenFromRequest();
        RevokedToken revokedToken = new RevokedToken();
        revokedToken.setToken(token);
        revokedToken.setRevokedAt(LocalDateTime.now());
        revokedTokenRepository.save(revokedToken);
    }

    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.findByToken(token).isPresent();
    }
}
