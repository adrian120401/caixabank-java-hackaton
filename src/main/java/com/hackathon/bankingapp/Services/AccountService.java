package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.NotFoundException;
import com.hackathon.bankingapp.Exceptions.UnauthorizedException;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtTokenProvider jwtProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void createPin(String pin, String password) {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user =  userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found."));

        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new UnauthorizedException("Bad credentials");
        }

        user.setPin(pin);
        userRepository.save(user);
    }

    public void updatePin(String oldPin, String newPin, String password) {
        String username = jwtProvider.getCurrentUserDetails().getUsername();
        User user =  userRepository.findByEmail(username).orElseThrow(() -> new NotFoundException("User not found."));

        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new UnauthorizedException("Bad credentials");
        }

        if (!user.getPin().equals(oldPin)) {
            throw new UnauthorizedException("Invalid old pin");
        }

        user.setPin(newPin);
        userRepository.save(user);
    }
}
