package com.eeit175.EEIT175.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    @Autowired
    private UsersService usersService;

    @PostMapping("/batch-update-passwords")
    public ResponseEntity<?> batchUpdatePasswords() {
        try {
            usersService.batchUpdatePasswords();
            return ResponseEntity.ok()
                    .body(Map.of("message", "所有用戶密碼已成功更新"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "更新密碼時發生錯誤：" + e.getMessage()));
        }
    }
}