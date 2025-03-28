package com.eeit1475th.eshop.member.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eeit1475th.eshop.member.entity.PasswordResetToken;
import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.member.repository.PasswordResetTokenRepository;
import com.eeit1475th.eshop.member.repository.UsersRepository;

@Service
public class PasswordResetService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void requestPasswordReset(String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("找不到該郵箱對應的用戶"));

        // 生成新的重置令牌
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        token.setUsed(false);

        tokenRepository.save(token);
        emailService.sendPasswordResetEmail(email, token.getToken());
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("無效的重置令牌"));

        if (resetToken.isUsed()) {
            throw new RuntimeException("此重置令牌已被使用");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("重置令牌已過期");
        }

        Users user = resetToken.getUser();
        user.setPassword(newPassword); // 注意：這裡需要加密密碼

        usersRepository.save(user);
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
}