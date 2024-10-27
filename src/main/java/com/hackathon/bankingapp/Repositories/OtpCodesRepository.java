package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.OtpCodes;
import com.hackathon.bankingapp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpCodesRepository extends JpaRepository<OtpCodes, UUID> {
    Optional<OtpCodes> findByCode(String code);
    Optional<OtpCodes> findByUser(User user);
    Optional<OtpCodes> findByUserAndCode(User user, String code);
}
