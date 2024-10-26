package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.AccountInfoResponse;
import com.hackathon.bankingapp.DTO.UserResponseDTO;
import com.hackathon.bankingapp.Services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @RequestMapping("/user")
    public ResponseEntity<UserResponseDTO> getUser() {
        return ResponseEntity.ok(dashboardService.getUser());
    }

    @RequestMapping("/account")
    public ResponseEntity<AccountInfoResponse> getAccount() {
        return ResponseEntity.ok(dashboardService.getAccount());
    }

}
