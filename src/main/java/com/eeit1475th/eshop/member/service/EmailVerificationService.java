package com.eeit1475th.eshop.member.service;

import com.eeit1475th.eshop.member.entity.EmailVerification;
import com.eeit1475th.eshop.member.repository.EmailVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmailVerificationService {

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void saveVerificationToken(String email) {
        // 删除该邮箱的所有旧验证记录
        emailVerificationRepository.deleteByEmail(email);

        // 生成6位數驗證碼
        String verificationCode = generateVerificationCode();

        // 設置過期時間（5分鐘後）
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        // 創建新的驗證記錄
        EmailVerification verification = new EmailVerification();
        verification.setEmail(email);
        verification.setToken(verificationCode);
        verification.setExpiresAt(expiresAt);
        verification.setVerified(false);

        // 保存到資料庫
        emailVerificationRepository.save(verification);

        // 發送驗證碼郵件
        emailService.sendVerificationEmail(email, verificationCode);
    }

    @Transactional
    public boolean verifyToken(String email, String token) {
        EmailVerification verification = emailVerificationRepository.findByEmail(email)
                .orElse(null);

        if (verification != null &&
                !verification.isVerified() &&
                verification.getExpiresAt().isAfter(LocalDateTime.now()) &&
                verification.getToken().equals(token)) {

            verification.setVerified(true);
            emailVerificationRepository.save(verification);
            return true;
        }

        return false;
    }

    public boolean isEmailVerified(String email) {
        return emailVerificationRepository.findByEmailAndIsVerifiedTrue(email).isPresent();
    }

    public String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}