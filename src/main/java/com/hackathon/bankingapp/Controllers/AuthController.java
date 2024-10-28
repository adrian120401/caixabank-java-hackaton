package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.PasswordResetDTO;
import com.hackathon.bankingapp.DTO.SendOTPDTO;
import com.hackathon.bankingapp.DTO.VerifyOTPDTO;
import com.hackathon.bankingapp.Services.AuthService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/password-reset/send-otp")
    public ResponseEntity<Map<String, String>> sendOPT(@RequestBody @Valid SendOTPDTO sendOTPDTO) {

        String email = authService.sendOTP(sendOTPDTO.getIdentifier());

        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent successfully to: " + email);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOTP(@RequestBody @Valid VerifyOTPDTO verifyOTPDTO) {

        String token = authService.verifyOTP(verifyOTPDTO.getIdentifier(), verifyOTPDTO.getOtp());

        Map<String, String> response = new HashMap<>();
        response.put("passwordResetToken", token);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @Valid PasswordResetDTO passwordResetDTO) {
        authService.resetPassword(passwordResetDTO.getIdentifier(), passwordResetDTO.getResetToken(),
                passwordResetDTO.getNewPassword());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully");
        return ResponseEntity.ok().body(response);
    }

}
