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

    @Autowired
    private Set<String> verifiedEmails;

    @Autowired
    private Map<String, String> verificationTokens;

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

<<<<<<< HEAD
    public boolean verifyToken(String email, String code) {
        // 獲取該郵箱的所有未驗證且未過期的記錄
        List<EmailVerification> verifications = emailVerificationRepository
                .findByEmailAndIsVerifiedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        email, LocalDateTime.now());

        if (!verifications.isEmpty()) {
            EmailVerification verification = verifications.get(0); // 獲取最新的記錄
            if (verification.getToken().equals(code)) {
                verification.setVerified(true);
                emailVerificationRepository.save(verification);
                return true;
            }
=======
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
>>>>>>> f447786f764262bb7788a6076d7ac2e6536f6cac
        }

        return false;
    }

    public boolean isEmailVerified(String email) {
        return emailVerificationRepository.findByEmailAndIsVerifiedTrue(email).isPresent();
    }

    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}