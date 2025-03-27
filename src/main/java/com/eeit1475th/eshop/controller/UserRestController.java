package com.eeit1475th.eshop.controller;

import com.eeit1475th.eshop.member.dto.ApiResponse;
import com.eeit1475th.eshop.member.dto.LoginRequest;
import com.eeit1475th.eshop.member.dto.PasswordUpdateRequest;
import com.eeit1475th.eshop.member.dto.UsersDTO;
import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.member.service.EmailVerificationService;
import com.eeit1475th.eshop.member.service.UsersService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerificationCode(@RequestParam String email) {
        try {
            emailVerificationService.saveVerificationToken(email);
            return ResponseEntity.ok(new ApiResponse(true, "驗證碼已發送到您的郵箱"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/register-with-verification")
    public ResponseEntity<?> registerWithVerification(@RequestBody UsersDTO userDTO) {
        try {
            // 驗證驗證碼
            boolean verified = emailVerificationService.verifyToken(userDTO.getEmail(), userDTO.getVerificationCode());
            if (!verified) {
                return ResponseEntity.badRequest().body(new ApiResponse(false, "驗證碼無效或已過期"));
            }

            // 創建用戶
            Users user = usersService.createUser(userDTO);
            return ResponseEntity.ok(new ApiResponse(true, "註冊成功", "/login"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsersDTO userDTO) {
        try {
            // 創建用戶
            Users user = usersService.createUser(userDTO);

            // 發送驗證郵件
            emailVerificationService.saveVerificationToken(user.getEmail());

            return ResponseEntity.ok(new ApiResponse(true, "註冊成功，請檢查您的郵箱進行驗證", "/login"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String email, @RequestParam String code) {
        try {
            boolean verified = emailVerificationService.verifyToken(email, code);
            if (verified) {
                return ResponseEntity.ok().body(Map.of("message", "驗證成功"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "驗證碼無效或已過期"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // 執行登入
            String token = usersService.login(loginRequest.getEmail(), loginRequest.getPassword());

            // 獲取用戶信息
            Users user = usersService.getUserByEmail(loginRequest.getEmail());

            // 轉換為DTO
            UsersDTO userDTO = new UsersDTO();
            BeanUtils.copyProperties(user, userDTO);
            userDTO.setPassword(null); // 不返回密碼

            // 返回成功響應
            return ResponseEntity.ok(new ApiResponse(true, "登入成功", Map.of(
                    "token", token,
                    "user", userDTO,
                    "redirectUrl", "/")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            Users user = usersService.getUserByToken(token);
            return ResponseEntity.ok(new ApiResponse(true, "獲取成功", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token,
            @RequestBody UsersDTO userDTO) {
        try {
            Users user = usersService.updateUser(token, userDTO);
            return ResponseEntity.ok(new ApiResponse(true, "更新成功", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PutMapping("/profile/password")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String token,
            @RequestBody PasswordUpdateRequest request) {
        try {
            usersService.updatePassword(token, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse(true, "密碼更新成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            usersService.logout(token);
            return ResponseEntity.ok(new ApiResponse(true, "登出成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }
}