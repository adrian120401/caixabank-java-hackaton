package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.OtpCodes;
import com.hackathon.bankingapp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpCodesRepository extends JpaRepository<OtpCodes, UUID> {
    Optional<OtpCodes> findByCode(String code);
    Optional<OtpCodes> findByUser(User user);
    Optional<OtpCodes> findByUserAndCode(User user, String code);
}
