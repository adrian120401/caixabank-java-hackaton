package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.Entities.OtpCodes;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.BadRequestException;
import com.hackathon.bankingapp.Repositories.OtpCodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OTPService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpCodesRepository otpCodesRepository;

    public String generateOTP(User user) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        OtpCodes otpCodes = new OtpCodes();
        otpCodes.setCode(otp);
        otpCodes.setUser(user);
        otpCodesRepository.save(otpCodes);
        return otp;
    }

    public void sendOTP(String otp, String email) {
        emailService.sendEmail(email, "Your OTP for Password Reset", "OTP:" + otp);
    }

    public OtpCodes getByUserAndCode(User user, String code) {
        return otpCodesRepository.findByUserAndCode(user, code).orElseThrow(() -> new BadRequestException("Invalid OTP"));
    }
}
