package com.eeit1475th.eshop.member.repository;

import com.eeit1475th.eshop.member.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByToken(String token);

    Optional<EmailVerification> findByEmail(String email);

    void deleteByEmail(String email);
}