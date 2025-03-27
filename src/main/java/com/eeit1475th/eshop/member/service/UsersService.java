package com.eeit1475th.eshop.member.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eeit1475th.eshop.member.dto.UsersDTO;
import com.eeit1475th.eshop.member.dto.UserVipDTO;
import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.member.entity.UserVip;
import com.eeit1475th.eshop.member.entity.UserVipHistory;
import com.eeit1475th.eshop.member.repository.UsersRepository;
import com.eeit1475th.eshop.member.repository.UserVipHistoryRepository;
import com.eeit1475th.eshop.member.repository.UserVipRepository;

import jakarta.servlet.http.HttpSession;

@Service
@Transactional
public class UsersService {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private UserVipRepository userVipRepository;

    @Autowired
    private UserVipHistoryRepository userVipHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private JwtService jwtService;

    // 創建用戶
    @Transactional
    public Users createUser(UsersDTO userDTO) {
        // 檢查用戶名是否已存在
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("用戶名已存在");
        }

        // 檢查郵箱是否已存在
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("郵箱已被使用");
        }

        // 創建新用戶
        Users user = new Users();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setFullName(userDTO.getFullName());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());

        // 保存用戶
        return userRepository.save(user);
    }

    // 驗證登入
    public Users validateLogin(String username, String password) {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用戶名或密碼錯誤"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用戶名或密碼錯誤");
        }

        return user;
    }

    // 登入
    public String login(String email, String password) {
        // 查找用戶
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        // 驗證密碼
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密碼錯誤");
        }

        // 生成 JWT token
        return jwtService.generateToken(user.getUsername(), user.getUserId());
    }

    // 更新用戶資料
    public Users updateUser(Integer id, UsersDTO userDTO) {
        Users existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        // 檢查用戶名是否被其他用戶使用
        if (!existingUser.getUsername().equals(userDTO.getUsername()) &&
                userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("用戶名已存在");
        }

        // 檢查郵箱是否被其他用戶使用
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(existingUser.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("郵箱已被使用");
        }

        // 更新用戶資料
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setFullName(userDTO.getFullName());
        existingUser.setPhone(userDTO.getPhone());
        existingUser.setAddress(userDTO.getAddress());

        return userRepository.save(existingUser);
    }

    // 獲取所有用戶
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    // 根據ID獲取用戶
    public Users getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
    }

    // 根據用戶名獲取用戶
    public Users findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    // 刪除用戶
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("用戶不存在");
        }
        userRepository.deleteById(id);
    }

    // 登出
    public void logout(HttpSession session) {
        session.invalidate();
    }

    // 創建帶VIP信息的用戶
    @Transactional
    public Users createUserWithVip(UsersDTO userDTO, UserVipDTO vipDTO) {
        // 1. 創建用戶
        Users user = createUser(userDTO);

        // 2. 如果有VIP信息，創建VIP記錄
        if (vipDTO != null) {
            UserVip vip = new UserVip();
            BeanUtils.copyProperties(vipDTO, vip);
            vip.setUsers(user);
            vip = userVipRepository.save(vip);
            user.setUserVip(vip);

            // 3. 創建VIP歷史記錄
            UserVipHistory history = new UserVipHistory();
            history.setUsers(user);
            history.setVipLevel(vipDTO.getVipLevel());
            history.setStartDate(vipDTO.getStartDate());
            history.setEndDate(vipDTO.getEndDate());
            history.setVipPhoto(vipDTO.getVipPhoto());
            userVipHistoryRepository.save(history);
        }

        return user;
    }

    public void updateUserVip(Integer memberId, UserVipDTO vipDTO1) {
        // TODO Auto-generated method stub
    }

    public void addVipHistory(Integer memberId, UserVipDTO historyDTO1) {
        // TODO Auto-generated method stub
    }

    // 根據郵箱查詢
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
    }

    // 重置用戶密碼
    public Users resetPassword(Integer id, String newPassword, boolean isEncrypted) {
        try {
            Users user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("用戶不存在 ID: " + id));

            if (newPassword == null || newPassword.trim().isEmpty()) {
                throw new RuntimeException("密碼不能為空");
            }

            // 如果密碼已經加密，直接設置；否則進行加密
            if (isEncrypted) {
                user.setPassword(newPassword);
            } else {
                user.setPassword(passwordEncoder.encode(newPassword));
            }
            System.out.println("用戶 " + user.getUsername() + " 的密碼已更新");

            return userRepository.save(user);
        } catch (Exception e) {
            System.out.println("重置密碼時發生錯誤 - ID: " + id + ", 錯誤: " + e.getMessage());
            throw new RuntimeException("重置密碼失敗: " + e.getMessage());
        }
    }

    // 重置為指定的明文密碼
    public Users resetToPlainPassword(Integer id, String plainPassword) {
        return resetPassword(id, plainPassword, false);
    }

    // 批次更新密碼
    public List<Map<String, Object>> batchUpdatePasswords() {
        List<Map<String, Object>> results = new ArrayList<>();
        List<Users> users = getAllUsers();

        for (Users user : users) {
            Map<String, Object> result = new HashMap<>();
            result.put("username", user.getUsername());
            result.put("memberId", user.getUserId());

            try {
                String currentPassword = user.getPassword();
                // 強制更新所有密碼
                resetPassword(user.getUserId(), currentPassword, true);
                result.put("status", "success");
                result.put("message", "密碼已更新為加密格式");
            } catch (Exception e) {
                System.out.println("處理用戶時發生錯誤 - " + user.getUsername() + ": " + e.getMessage());
                result.put("status", "error");
                result.put("message", e.getMessage());
            }

            results.add(result);
        }

        return results;
    }

    public Users getUserByToken(String token) {
        // 从 token 中获取用户 ID
        Integer userId = jwtService.extractUserId(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
    }

    @Transactional
    public Users updateUser(String token, UsersDTO userDTO) {
        Users user = getUserByToken(token);

        // 更新用戶信息
        user.setFullName(userDTO.getFullName());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());

        return userRepository.save(user);
    }

    @Transactional
    public void updatePassword(String token, String oldPassword, String newPassword) {
        Users user = getUserByToken(token);

        // 驗證舊密碼
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("舊密碼錯誤");
        }

        // 更新密碼
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void logout(String token) {
        // TODO: 實現登出邏輯，例如使 token 失效
    }
}