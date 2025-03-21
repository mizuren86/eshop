package com.eeit175.EEIT175.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsersService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsersRepository usersRepository;

    @Transactional
    public void batchUpdatePasswords() {
        // 獲取所有用戶
        List<Users> users = usersRepository.findAll();

        for (Users user : users) {
            // 獲取原始密碼（假設密碼是用戶名加上"123"）
            String originalPassword = user.getUsername() + "123";

            // 使用 BCrypt 加密密碼
            String encodedPassword = passwordEncoder.encode(originalPassword);

            // 更新用戶密碼
            user.setPassword(encodedPassword);
            usersRepository.save(user);

            System.out.println("已更新用戶 " + user.getUsername() + " 的密碼");
        }
    }
}