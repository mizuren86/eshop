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

    // 發送郵件
    private void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    // 發送驗證碼郵件
    public void sendVerificationEmail(String to, String verificationCode) {
        String subject = "驗證您的郵箱 - eShop";
        String content = String.format(
                "親愛的用戶：\n\n" +
                        "感謝您註冊 eShop！\n\n" +
                        "您的驗證碼是：%s\n\n" +
                        "請在5分鐘內使用此驗證碼完成郵箱驗證。\n\n" +
                        "如果您沒有註冊 eShop，請忽略此郵件。\n\n" +
                        "祝您使用愉快！\n" +
                        "eShop 團隊",
                verificationCode);

        sendEmail(to, subject, content);
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

    // 發送重置密碼郵件
    public void sendResetPasswordEmail(String to, String verificationCode) {
        String subject = "重置密碼 - eShop";
        String content = String.format(
                "親愛的用戶：\n\n" +
                        "您收到這封郵件是因為您請求重置密碼。\n\n" +
                        "您的驗證碼是：%s\n\n" +
                        "請在5分鐘內使用此驗證碼完成密碼重置。\n\n" +
                        "如果您沒有請求重置密碼，請忽略此郵件。\n\n" +
                        "祝您使用愉快！\n" +
                        "eShop 團隊",
                verificationCode);

        sendEmail(to, subject, content);
    }
}