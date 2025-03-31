package com.eeit1475th.eshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eeit1475th.eshop.member.service.UsersService;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    // 發送重置密碼郵件
    @PostMapping("/forgot-password")
    @ResponseBody
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            usersService.sendResetPasswordEmail(email);
            return ResponseEntity.ok(Map.of("message", "重置密碼郵件已發送"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 重置密碼
    @PostMapping("/reset-password")
    @ResponseBody
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String verificationCode = request.get("verificationCode");
            String newPassword = request.get("newPassword");

            usersService.resetPassword(email, verificationCode, newPassword);
            return ResponseEntity.ok(Map.of("message", "密碼重置成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}