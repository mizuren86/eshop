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
import com.eeit1475th.eshop.member.service.UsersService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

	@Autowired
	private UsersService usersService;

	@Autowired
	private EmailVerificationService emailVerificationService;

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
				Path uploadDir = Paths.get("src/main/resources/static/uploads");
				if (!Files.exists(uploadDir)) {
					Files.createDirectories(uploadDir);
				}
				Path filePath = uploadDir.resolve(fileName);
				Files.copy(userPhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				userDTO.setUserPhoto("/uploads/" + fileName);
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
			@RequestParam(value = "redirect", required = false) String redirect) { // 年測試
		try {
			String token = usersService.login(request.getEmail(), request.getPassword());
			Users user = usersService.getUserByEmail(request.getEmail());

			// 将用户信息存储在会话中
			session.setAttribute("user", user);

			// 年測試 start //
			session.setAttribute("token", token);

			session.setAttribute("userId", user.getUserId());
			// 檢查是否存在未登入時暫存的購物車
			@SuppressWarnings("unchecked")
			List<CartItemsDTO> tempCart = (List<CartItemsDTO>) session.getAttribute("tempCart");
			if (tempCart != null && !tempCart.isEmpty()) {
				// 將每個項目合併到該會員的購物車中
				for (CartItemsDTO item : tempCart) {
					shoppingCartService.addToCart(user.getUserId(), item.getProduct().getProductId(),
							item.getQuantity());
				}
				// 清除暫存購物車
				session.removeAttribute("tempCart");
			}
			// 年測試 end //

			Map<String, Object> responseData = new HashMap<>();
			responseData.put("token", token);
			responseData.put("user", user);

			responseData.put("redirect", redirect != null ? redirect : "/"); // 年測試

			return ResponseEntity.ok(new ApiResponse(true, "登入成功", responseData));
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
	public ResponseEntity<?> logout(HttpSession session) {
		try {
			System.out.println("正在處理登出請求...");
			System.out.println("Session ID: " + session.getId());

			// 清除会话
			session.invalidate();
			System.out.println("會話已清除");

			return ResponseEntity.ok(new ApiResponse(true, "登出成功"));
		} catch (Exception e) {
			System.out.println("登出過程發生錯誤: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.ok(new ApiResponse(true, "登出成功")); // 即使发生错误也返回成功
		}
	}

	@GetMapping("/current")
	public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
		try {
			Users user = usersService.getUserByToken(token);
			if (user != null) {
				return ResponseEntity.ok(new ApiResponse(true, "獲取成功", user));
			} else {
				return ResponseEntity.badRequest().body(new ApiResponse(false, "用戶不存在"));
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
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
}