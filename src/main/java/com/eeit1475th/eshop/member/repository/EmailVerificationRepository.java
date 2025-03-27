package com.eeit1475th.eshop.member.repository;

import com.eeit1475th.eshop.member.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByToken(String token);

    Optional<EmailVerification> findByEmail(String email);

<<<<<<< HEAD
    List<EmailVerification> findByEmailAndIsVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(String email,
            LocalDateTime now);

    Optional<EmailVerification> findByEmailAndIsVerifiedTrue(String email);
=======
    void deleteByEmail(String email);
>>>>>>> f447786f764262bb7788a6076d7ac2e6536f6cac
}