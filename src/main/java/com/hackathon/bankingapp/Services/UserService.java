package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.DTO.UserRequestDTO;
import com.hackathon.bankingapp.DTO.UserResponseDTO;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.BadRequestException;
import com.hackathon.bankingapp.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserResponseDTO register(UserRequestDTO userRequestDTO) {
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists.");
        }
        if (userRepository.findByPhoneNumber(userRequestDTO.getPhoneNumber()).isPresent()) {
            throw new BadRequestException("Phone number already exists.");
        }

        User user = new User();
        user.setName(userRequestDTO.getName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPhoneNumber(userRequestDTO.getPhoneNumber());
        user.setAddress(userRequestDTO.getAddress());
        user.setHashedPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        userRepository.save(user);

        return new UserResponseDTO(user.getName(), user.getEmail(), user.getPhoneNumber(), user.getAddress(), user.getAccountNumber(), user.getHashedPassword());
    }
}
