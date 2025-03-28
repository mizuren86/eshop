package com.eeit1475th.eshop.member.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    @Autowired
    private EmailService emailService;

    private final Map<String, VerificationCode> verificationCodes = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public void sendVerificationCode(String email) {
        String code = generateVerificationCode();
        verificationCodes.put(email, new VerificationCode(code, LocalDateTime.now().plusMinutes(5)));
        emailService.sendVerificationEmail(email, code);
    }

    public boolean verifyCode(String email, String code) {
        VerificationCode verificationCode = verificationCodes.get(email);
        if (verificationCode == null || verificationCode.isExpired()) {
            return false;
        }
        boolean isValid = verificationCode.getCode().equals(code);
        if (isValid) {
            verificationCodes.remove(email);
        }
        return isValid;
    }

    private String generateVerificationCode() {
        return String.format("%06d", random.nextInt(1000000));
    }

    private static class VerificationCode {
        private final String code;
        private final LocalDateTime expiryDate;

        public VerificationCode(String code, LocalDateTime expiryDate) {
            this.code = code;
            this.expiryDate = expiryDate;
        }

        public String getCode() {
            return code;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryDate);
        }
    }
}