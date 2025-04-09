package com.eeit1475th.eshop.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eeit1475th.eshop.cart.dto.CartItemsDTO;
import com.eeit1475th.eshop.cart.service.ShoppingCartService;
import com.eeit1475th.eshop.member.dto.ApiResponse;
import com.eeit1475th.eshop.member.dto.LoginRequest;
import com.eeit1475th.eshop.member.dto.PasswordUpdateRequest;
import com.eeit1475th.eshop.member.dto.UsersDTO;
import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.member.service.EmailVerificationService;
import com.eeit1475th.eshop.member.service.JwtService;
import com.eeit1475th.eshop.member.service.UsersService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

	@Autowired
	private UsersService usersService;

	@Autowired
	private EmailVerificationService emailVerificationService;

	@Autowired
	private JwtService jwtService;

	// 年測試 //
	@Autowired
	private ShoppingCartService shoppingCartService;
	// 年測試 //

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
	public ResponseEntity<?> register(@RequestParam("username") String username,
			@RequestParam("email") String email,
			@RequestParam("password") String password,
			@RequestParam("verificationCode") String verificationCode,
			@RequestParam("phone") String phone,
			@RequestParam("address") String address,
			@RequestParam(value = "userPhoto", required = false) MultipartFile userPhoto) {
		try {
			// 先验证验证码
			boolean verified = emailVerificationService.verifyToken(email, verificationCode);
			if (!verified) {
				return ResponseEntity.badRequest().body(new ApiResponse(false, "驗證碼無效或已過期"));
			}

			// 创建用户DTO
			UsersDTO userDTO = new UsersDTO();
			userDTO.setUsername(username);
			userDTO.setEmail(email);
			userDTO.setPassword(password);
			userDTO.setFullName(username);
			userDTO.setPhone(phone);
			userDTO.setAddress(address);

			// 处理头像上传
			if (userPhoto != null && !userPhoto.isEmpty()) {
				String fileName = UUID.randomUUID().toString() + "_" + userPhoto.getOriginalFilename();
				Path uploadDir = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "img",
						"users");
				if (!Files.exists(uploadDir)) {
					Files.createDirectories(uploadDir);
				}
				Path filePath = uploadDir.resolve(fileName);
				Files.copy(userPhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				userDTO.setUserPhoto("img/users/" + fileName);
			}

			// 创建用户
			Users user = usersService.createUser(userDTO);

			return ResponseEntity.ok(new ApiResponse(true, "註冊成功", "/login"));
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
	public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session,
			@RequestParam(value = "redirect", required = false) String redirect) {
		try {
			System.out.println("收到登入請求");
			System.out.println("Email: " + request.getEmail());

			String token = usersService.login(request.getEmail(), request.getPassword());
			Users user = usersService.getUserByEmail(request.getEmail());

			System.out.println("用戶登入成功");
			System.out.println("用戶ID: " + user.getUserId());
			System.out.println("用戶名: " + user.getUsername());
			System.out.println("Token: " + token);

			// 将用户信息存储在会话中
			session.setAttribute("user", user);
			session.setAttribute("token", token);
			session.setAttribute("userId", user.getUserId());

			// 檢查是否存在未登入時暫存的購物車
			@SuppressWarnings("unchecked")
			List<CartItemsDTO> tempCart = (List<CartItemsDTO>) session.getAttribute("tempCart");
			if (tempCart != null && !tempCart.isEmpty()) {
				System.out.println("發現暫存購物車，開始合併");
				// 將每個項目合併到該會員的購物車中
				for (CartItemsDTO item : tempCart) {
					shoppingCartService.addToCart(user.getUserId(), item.getProduct().getProductId(),
							item.getQuantity());
				}
				// 清除暫存購物車
				session.removeAttribute("tempCart");
				System.out.println("購物車合併完成");
			}

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("token", token);
			responseData.put("user", user);
			responseData.put("redirect", redirect != null ? redirect : "/");

			System.out.println("準備返回響應數據");
			return ResponseEntity.ok(new ApiResponse(true, "登入成功", responseData));
		} catch (Exception e) {
			System.out.println("登入失敗: " + e.getMessage());
			e.printStackTrace();
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
	public ResponseEntity<?> updateProfile(
			@RequestHeader("Authorization") String token,
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "fullName", required = false) String fullName,
			@RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "address", required = false) String address,
			@RequestParam(value = "userPhoto", required = false) MultipartFile userPhoto) {
		try {
			// 验证token并获取用户ID
			Integer userId = jwtService.extractUserId(token);
			if (userId == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("無效的token");
			}

			// 获取用户信息
			Users user = usersService.getUserById(userId);
			if (user == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("用戶不存在");
			}

			// 创建UsersDTO对象
			UsersDTO userDTO = new UsersDTO();
			userDTO.setMemberId(userId);
			userDTO.setUsername(username != null ? username : user.getUsername());
			userDTO.setEmail(email != null ? email : user.getEmail());
			userDTO.setFullName(fullName != null ? fullName : user.getFullName());
			userDTO.setPhone(phone != null ? phone : user.getPhone());
			userDTO.setAddress(address != null ? address : user.getAddress());
			userDTO.setUserPhoto(user.getUserPhoto());

			// 处理用户照片
			if (userPhoto != null && !userPhoto.isEmpty()) {
				String fileName = UUID.randomUUID().toString() + "_" + userPhoto.getOriginalFilename();
				Path uploadDir = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", "img",
						"users");
				if (!Files.exists(uploadDir)) {
					Files.createDirectories(uploadDir);
				}
				Path filePath = uploadDir.resolve(fileName);
				Files.copy(userPhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				userDTO.setUserPhoto("img/users/" + fileName);
			}

			// 保存更新后的用户信息
			Users updatedUser = usersService.updateUser(userId, userDTO);
			return ResponseEntity.ok(new ApiResponse(true, "個人資料更新成功", updatedUser));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse(false, "更新個人資料時發生錯誤：" + e.getMessage()));
		}
	}

	@GetMapping("/current")
	public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
		try {
			System.out.println("收到獲取當前用戶請求");
			System.out.println("原始Token: " + token);

			// 移除 "Bearer " 前綴（如果有的話）
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);
			}
			System.out.println("處理後的Token: " + token);

			Users user = usersService.getUserByToken(token);
			if (user != null) {
				System.out.println("成功獲取用戶信息");
				System.out.println("用戶ID: " + user.getUserId());
				System.out.println("用戶名: " + user.getUsername());
				System.out.println("郵箱: " + user.getEmail());

				// 確保加載關聯數據
				if (user.getUserVip() != null) {
					System.out.println("VIP等級: " + user.getUserVip().getVipLevel());
					System.out.println("VIP到期日: " + user.getUserVip().getEndDate());
				}

				return ResponseEntity.ok(new ApiResponse(true, "獲取成功", user));
			} else {
				System.out.println("未找到用戶");
				return ResponseEntity.status(401).body(new ApiResponse(false, "用戶不存在或token無效"));
			}
		} catch (Exception e) {
			System.out.println("獲取用戶信息時發生錯誤: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(401).body(new ApiResponse(false, "token無效或已過期"));
		}
	}

	@GetMapping("/login")
	public String showLoginPage() {
		return "login";
	}

	@GetMapping("/register")
	public String showRegisterPage() {
		return "register";
	}

	@GetMapping("/forgot-password")
	public String showForgotPasswordPage() {
		return "forgot-password";
	}

	@PostMapping("/send-verification-code")
	public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request,
			@RequestHeader("Authorization") String token) {
		try {
			String email = request.get("email");
			if (email == null || email.isEmpty()) {
				return ResponseEntity.badRequest().body(new ApiResponse(false, "請提供電子郵件"));
			}

			// 驗證郵箱是否屬於當前用戶
			Users user = usersService.getUserByToken(token);
			if (user == null || !user.getEmail().equals(email)) {
				return ResponseEntity.badRequest().body(new ApiResponse(false, "電子郵件與帳號不符"));
			}

			// 生成驗證碼
			String verificationCode = usersService.generateVerificationCode(email);

			// 發送驗證碼郵件
			usersService.sendVerificationEmail(email, verificationCode);

			return ResponseEntity.ok(new ApiResponse(true, "驗證碼已發送"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
		}
	}

	@PutMapping("/verify-and-update-password")
	public ResponseEntity<?> verifyAndUpdatePassword(@RequestBody Map<String, String> request,
			@RequestHeader("Authorization") String token) {
		try {
			String code = request.get("code");
			String newPassword = request.get("newPassword");

			if (code == null || code.isEmpty() || newPassword == null || newPassword.isEmpty()) {
				return ResponseEntity.badRequest().body(new ApiResponse(false, "請提供驗證碼和新密碼"));
			}

			// 驗證碼驗證並更新密碼
			boolean success = usersService.verifyAndUpdatePassword(token, code, newPassword);
			if (success) {
				return ResponseEntity.ok(new ApiResponse(true, "密碼更新成功"));
			} else {
				return ResponseEntity.badRequest().body(new ApiResponse(false, "驗證碼無效或已過期"));
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
		}
	}

	@PutMapping("/profile/password")
	public ResponseEntity<?> updatePassword(
			@RequestHeader("Authorization") String token,
			@RequestBody PasswordUpdateRequest request) {
		try {
			// 移除 "Bearer " 前綴（如果有的話）
			if (token != null && token.startsWith("Bearer ")) {
				token = token.substring(7);
			}

			// 更新密碼
			usersService.updatePassword(token, request.getOldPassword(), request.getNewPassword());
			return ResponseEntity.ok(new ApiResponse(true, "密碼更新成功"));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
		}
	}
}