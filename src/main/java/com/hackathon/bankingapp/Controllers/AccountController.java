package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.Services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/pin/create")
    public ResponseEntity<Map<String, String>> createPint(@RequestBody Map<String, Object> requestBody){
        String pin = (String) requestBody.get("pin");
        String password = (String) requestBody.get("password");

        if (pin == null || pin.isEmpty() || password == null || password.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Pin and password are required");
            return ResponseEntity.badRequest().body(response);
        }
        accountService.createPin(pin, password);

        Map<String, String> response = new HashMap<>();
        response.put("msg", "PIN created successfully");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/pin/update")
    public ResponseEntity<Map<String, String>> updatePint(@RequestBody Map<String, Object> requestBody){
        String oldPin = (String) requestBody.get("oldPin");
        String newPin = (String) requestBody.get("newPin");
        String password = (String) requestBody.get("password");

        if (oldPin == null || oldPin.isEmpty() || newPin == null || newPin.isEmpty() || password == null || password.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Pin and password are required");
            return ResponseEntity.badRequest().body(response);
        }
        accountService.updatePin(oldPin, newPin, password);

        Map<String, String> response = new HashMap<>();
        response.put("msg", "PIN updated successfully");
        return ResponseEntity.ok().body(response);
    }
}
