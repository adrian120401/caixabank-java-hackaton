package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.Entities.OtpCodes;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Repositories.OtpCodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OTPService {
    @Autowired
    private JavaMailSender javaMailSender;

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
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP for Password Reset");
        message.setText("OTP: " + otp);
        javaMailSender.send(message);
    }

    public OtpCodes getByUserAndCode(User user, String code) {
        return otpCodesRepository.findByUserAndCode(user, code).orElseThrow(() -> new RuntimeException("Invalid OTP"));
    }
}
