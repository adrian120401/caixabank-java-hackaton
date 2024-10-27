package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.DTO.LoginRequestDTO;
import com.hackathon.bankingapp.DTO.UserRequestDTO;
import com.hackathon.bankingapp.DTO.UserResponseDTO;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.BadRequestException;
import com.hackathon.bankingapp.Exceptions.NotFoundException;
import com.hackathon.bankingapp.Exceptions.UnauthorizedException;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    @Autowired
    private JwtTokenProvider jwtProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserResponseDTO register(UserRequestDTO userRequestDTO) {
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }
        if (userRepository.findByPhoneNumber(userRequestDTO.getPhoneNumber()).isPresent()) {
            throw new BadRequestException("Phone number already exists");
        }

        validatePassword(userRequestDTO.getPassword());

        User user = new User();
        user.setName(userRequestDTO.getName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPhoneNumber(userRequestDTO.getPhoneNumber());
        user.setAddress(userRequestDTO.getAddress());
        user.setHashedPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        userRepository.save(user);

        return new UserResponseDTO(user.getName(), user.getEmail(), user.getPhoneNumber(), user.getAddress(), user.getAccountNumber(), user.getHashedPassword());
    }

    public Map<String, String> login(LoginRequestDTO loginRequestDTO) {
        User user = getUserByIdentifier(loginRequestDTO.getIdentifier());

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getHashedPassword())) {
            throw new UnauthorizedException("Bad credentials");
        }
        String token = jwtProvider.generateToken(user.getEmail());
        return Map.of("token", token);
    }

    public User getUserByIdentifier(String identifier) {
        return userRepository.findByEmail(identifier)
                .orElseGet(() -> {
                    try {
                        UUID accountNumber = UUID.fromString(identifier);
                        return userRepository.findByAccountNumber(accountNumber)
                                .orElseThrow(() -> new NotFoundException("User not found for the given identifier: " + identifier));
                    } catch (IllegalArgumentException e) {
                        throw new NotFoundException("User not found for the given identifier: " + identifier);
                    }
                });
    }

    private void validatePassword(String password) {
        if (password.length() < 8 || password.length() > 127) {
            throw new BadRequestException("Password must be between 8 and 127 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new BadRequestException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new BadRequestException("Password must contain at least one digit");
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            throw new BadRequestException("Password must contain at least one special character");
        }
        if (password.contains(" ")) {
            throw new BadRequestException("Password cannot contain whitespace");
        }
    }
}
