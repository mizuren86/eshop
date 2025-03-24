package com.eeit1475th.eshop.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("您的驗證碼");
        message.setText("親愛的用戶：\n\n" +
                "您的驗證碼是：" + verificationCode + "\n\n" +
                "此驗證碼將在 5 分鐘後失效。\n\n" +
                "如果您沒有請求此驗證碼，請忽略此郵件。\n\n" +
                "祝您使用愉快！\n" +
                "eShop 團隊");

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String to, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("電子商城 - 密碼重置");
        message.setText("請點擊以下鏈接重置密碼：\n" +
                "http://localhost:8091/reset-password?token=" + resetToken + "\n" +
                "此鏈接有效期為30分鐘。");
        mailSender.send(message);
    }
}