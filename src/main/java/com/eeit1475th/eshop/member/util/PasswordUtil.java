package com.eeit1475th.eshop.member.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {
    private static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 密碼加密
    public static String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    // 密碼比對
    public static boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}