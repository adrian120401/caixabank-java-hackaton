package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.Entities.OtpCodes;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.BadRequestException;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OTPService otpService;

    @Autowired
    private JwtTokenProvider jwtProvider;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String sendOTP(String identifier) {
        User user = userService.getUserByIdentifier(identifier);

        String otp = otpService.generateOTP(user);
        otpService.sendOTP(otp, user.getEmail());
        return user.getEmail();
    }

    public String verifyOTP(String identifier, String otp) {
        User user = userService.getUserByIdentifier(identifier);
        OtpCodes otpCodes = otpService.getByUserAndCode(user, otp);
        if (!otpCodes.getCode().equals(otp)) {
            throw new BadRequestException("Invalid OTP");
        }

        return jwtProvider.generateToken(user.getEmail());
    }

    public void resetPassword(String identifier, String token,String password) {
        User user = userService.getUserByIdentifier(identifier);
        if (!jwtProvider.validateToken(token)) {
            throw new BadRequestException("Invalid reset token");
        }
        user.setHashedPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
