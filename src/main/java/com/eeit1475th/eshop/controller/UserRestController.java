package com.eeit1475th.eshop.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eeit1475th.eshop.member.dto.LoginDTO;
import com.eeit1475th.eshop.member.dto.RegisterDTO;
import com.eeit1475th.eshop.member.dto.UserVipDTO;
import com.eeit1475th.eshop.member.dto.UsersDTO;
import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.member.service.JwtService;
import com.eeit1475th.eshop.member.service.UsersService;
import com.eeit1475th.eshop.member.service.VerificationService;

@RestController
@RequestMapping("/api/users")
// @CrossOrigin(origins = "http://localhost:8091", allowCredentials = "true")
public class UserRestController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private JwtService jwtService;

    // 用戶註冊（返回 JSON）
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UsersDTO userDTO) {
        try {
            Users user = usersService.createUser(userDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("redirectUrl", "index");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 用戶登入
    // @PostMapping("/login")
    // public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
    // try {
    // Users user = usersService.validateLogin(loginDTO.getUsername(),
    // loginDTO.getPassword());
    // // 生成 JWT token
    // String token = jwtService.generateToken(user.getUsername());
    // // 建立 HTTP-only cookie
    // ResponseCookie cookie = ResponseCookie.from("jwt", token)
    // .httpOnly(true)
    // .secure(false) // 生產環境中建議設為 true
    // .path("/")
    // .maxAge(24 * 60 * 60) // 24 小時
    // .sameSite("Strict")
    // .build();
    //
    // Map<String, Object> response = new HashMap<>();
    // response.put("username", user.getUsername());
    // response.put("fullName", user.getFullName());
    // response.put("email", user.getEmail());
    //
    // return ResponseEntity.ok()
    // .header(HttpHeaders.SET_COOKIE, cookie.toString())
    // .body(response);
    // } catch (Exception e) {
    // return ResponseEntity.badRequest().body(e.getMessage());
    // }
    // }

    // 獲取當前登入用戶
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(@CookieValue(name = "jwt", required = false) String token) {
        if (token == null) {
            return ResponseEntity.badRequest().body("未登入");
        }
        try {
            String username = jwtService.extractUsername(token);
            Users user = usersService.findByUsername(username);
            if (user != null && jwtService.isTokenValid(token, username)) {
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.badRequest().body("未登入");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("未登入");
        }
    }

    // 用戶登出
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("登出成功");
    }

    // 更新用戶資料
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody UsersDTO userDTO) {
        try {
            Users updatedUser = usersService.updateUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 刪除用戶
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            usersService.deleteUser(id);
            return ResponseEntity.ok("用戶已刪除");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 獲取所有用戶
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<Users> users = usersService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 獲取用戶表格數據
    @GetMapping("/tables")
    public ResponseEntity<?> getTableData() {
        try {
            List<Users> users = usersService.getAllUsers();
            Map<String, Object> response = new HashMap<>();

            // 用戶表數據
            List<Map<String, Object>> usersTable = users.stream().map(user -> {
                Map<String, Object> userData = new HashMap<>();
                userData.put("memberId", user.getUserId());
                userData.put("username", user.getUsername());
                userData.put("email", user.getEmail());
                userData.put("fullName", user.getFullName());
                userData.put("phone", user.getPhone());
                userData.put("address", user.getAddress());
                return userData;
            }).distinct().collect(Collectors.toList());
            response.put("usersTable", usersTable);

            // VIP 表數據
            List<Map<String, Object>> vipTable = users.stream()
                    .filter(user -> user.getUserVip() != null)
                    .map(user -> {
                        Map<String, Object> vipData = new HashMap<>();
                        vipData.put("vipId", user.getUserVip().getVipId());
                        vipData.put("memberId", user.getUserId());
                        vipData.put("isVip", user.getUserVip().getIsVip());
                        vipData.put("vipLevel", user.getUserVip().getVipLevel());
                        vipData.put("startDate", user.getUserVip().getStartDate());
                        vipData.put("endDate", user.getUserVip().getEndDate());
                        vipData.put("vipPhoto", user.getUserVip().getVipPhoto());
                        return vipData;
                    }).distinct().collect(Collectors.toList());
            response.put("vipTable", vipTable);

            // VIP 歷史表數據
            List<Map<String, Object>> historyTable = users.stream()
                    .flatMap(user -> user.getVipHistories().stream())
                    .map(history -> {
                        Map<String, Object> historyData = new HashMap<>();
                        historyData.put("historyId", history.getHistoryId());
                        historyData.put("memberId", history.getUsers().getUserId());
                        historyData.put("vipLevel", history.getVipLevel());
                        historyData.put("startDate", history.getStartDate());
                        historyData.put("endDate", history.getEndDate());
                        historyData.put("vipPhoto", history.getVipPhoto());
                        return historyData;
                    }).distinct().collect(Collectors.toList());
            response.put("historyTable", historyTable);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 創建帶 VIP 信息的用戶
    @PostMapping("/with-vip")
    public ResponseEntity<?> createUserWithVip(@RequestBody Map<String, Object> request) {
        try {
            UsersDTO userDTO = new UsersDTO();
            Map<String, Object> userData = (Map<String, Object>) request.get("user");
            userDTO.setUsername((String) userData.get("username"));
            userDTO.setPassword((String) userData.get("password"));
            userDTO.setEmail((String) userData.get("email"));
            userDTO.setFullName((String) userData.get("fullName"));
            userDTO.setPhone((String) userData.get("phone"));
            userDTO.setAddress((String) userData.get("address"));

            UserVipDTO vipDTO = null;
            if (request.containsKey("vip")) {
                Map<String, Object> vipData = (Map<String, Object>) request.get("vip");
                vipDTO = new UserVipDTO();
                vipDTO.setIsVip((Boolean) vipData.get("isVip"));
                vipDTO.setVipLevel((Integer) vipData.get("vipLevel"));
                vipDTO.setStartDate(LocalDate.parse((String) vipData.get("startDate")));
                vipDTO.setEndDate(LocalDate.parse((String) vipData.get("endDate")));
                vipDTO.setVipPhoto((String) vipData.get("vipPhoto"));
            }

            Users user = usersService.createUserWithVip(userDTO, vipDTO);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 批次更新所有用戶密碼為加密格式
    @PostMapping("/batch-update-passwords")
    public ResponseEntity<?> batchUpdatePasswords() {
        try {
            System.out.println("開始批次更新密碼...");
            List<Map<String, Object>> results = usersService.batchUpdatePasswords();
            Map<String, Object> response = new HashMap<>();
            response.put("totalProcessed", results.size());
            response.put("results", results);
            System.out.println("批次更新密碼完成，共處理 " + results.size() + " 筆資料");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("批次更新密碼時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "批次更新密碼時發生錯誤: " + e.getMessage()));
        }
    }

    // 發送驗證碼
    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerification(@RequestBody Map<String, String> request) {
        try {
            String contact = request.get("email");
            if (contact == null) {
                contact = request.get("phone");
            }
            if (contact == null) {
                return ResponseEntity.badRequest().body("需要提供 email 或手機號碼");
            }
            String code = verificationService.generateCode(contact);
            System.out.println("驗證碼已發送到 " + contact + ": " + code);
            return ResponseEntity.ok("驗證碼已發送");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 驗證並註冊
    @PostMapping("/register-with-verification")
    public ResponseEntity<?> registerWithVerification(@RequestBody RegisterDTO registerDTO) {
        try {
            String contact = registerDTO.getEmail() != null ? registerDTO.getEmail() : registerDTO.getPhone();
            if (!verificationService.verifyCode(contact, registerDTO.getVerificationCode())) {
                return ResponseEntity.badRequest().body("驗證碼無效或已過期");
            }
            UsersDTO userDTO = new UsersDTO();
            userDTO.setUsername(registerDTO.getUsername());
            userDTO.setPassword(registerDTO.getPassword());
            userDTO.setEmail(registerDTO.getEmail());
            userDTO.setPhone(registerDTO.getPhone());
            userDTO.setFullName(registerDTO.getFullName());
            Users user = usersService.createUser(userDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("redirectUrl", "index");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 用戶登入
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            Users user = usersService.validateLogin(loginDTO.getUsername(), loginDTO.getPassword());
            // 生成 JWT token
            String token = jwtService.generateToken(user.getUsername(), user.getUserId());
            // 建立 HTTP-only cookie
            ResponseCookie cookie = ResponseCookie.from("jwt", token)
                    .httpOnly(true)
                    .secure(false) // 生產環境中建議設為 true
                    .path("/")
                    .maxAge(24 * 60 * 60) // 24 小時
                    .sameSite("Strict")
                    .build();

            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("fullName", user.getFullName());
            response.put("email", user.getEmail());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 重置用戶密碼
    @PostMapping("/reset-password/{userId}")
    public ResponseEntity<?> resetUserPassword(@PathVariable Integer userId, @RequestBody Map<String, String> request) {
        try {
            String newPassword = request.get("password");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("密碼不能為空");
            }

            Users user = usersService.resetToPlainPassword(userId, newPassword);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "密碼重置成功");
            response.put("username", user.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()));
        }
    }
}
