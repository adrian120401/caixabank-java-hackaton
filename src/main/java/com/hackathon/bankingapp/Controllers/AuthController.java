package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.Services.AuthService;
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
    public ResponseEntity<Map<String, String>> sendOPT(@RequestBody Map<String, Object> requestBody){
        String identifier = (String) requestBody.get("identifier");

        if (identifier == null || identifier.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Identifier is required");
            return ResponseEntity.badRequest().body(response);
        }

        String email = authService.sendOTP(identifier);

        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent successfully to: " + email);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/password-reset/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOTP(@RequestBody Map<String, Object> requestBody){
        String identifier = (String) requestBody.get("identifier");
        String otp = (String) requestBody.get("otp");

        if (identifier == null || identifier.isEmpty() || otp == null || otp.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Identifier and otp are required");
            return ResponseEntity.badRequest().body(response);
        }

        String token = authService.verifyOTP(identifier, otp);

        Map<String, String> response = new HashMap<>();
        response.put("passwordResetToken", token);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/password-reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, Object> requestBody){
        String identifier = (String) requestBody.get("identifier");
        String token = (String) requestBody.get("resetToken");
        String password = (String) requestBody.get("newPassword");

        if (identifier == null || identifier.isEmpty() || token == null || token.isEmpty() || password == null || password.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Identifier, token and password are required");
            return ResponseEntity.badRequest().body(response);
        }

        authService.resetPassword(identifier, token, password);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully");
        return ResponseEntity.ok().body(response);
    }

}
