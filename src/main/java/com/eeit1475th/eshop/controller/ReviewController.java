package com.eeit1475th.eshop.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.product.entity.Products;
import com.eeit1475th.eshop.product.repository.ProductsRepository;
import com.eeit1475th.eshop.review.dto.ReviewsDto;
import com.eeit1475th.eshop.review.dto.ReviewsDto4;
import com.eeit1475th.eshop.review.dto.ReviewsDto5;
import com.eeit1475th.eshop.review.dto.ReviewsDto6;
import com.eeit1475th.eshop.review.entity.Reviews;
import com.eeit1475th.eshop.review.repository.ReviewsRepository;
import com.eeit1475th.eshop.review.service.ReviewsService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewsService reviewsService;
	private final ReviewsRepository reviewsRepository;
	private final ProductsRepository productsRepository;

	@GetMapping("/product/{productId}")
	public String getReviewsByProductId(@PathVariable Integer productId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "3") int size, Model model) {
		model.addAttribute("ProductName", productsRepository.findById(productId).map(Products::getProductName) // 如果存在就轉換成
																												// productName
				.orElse("尚未命名"));

		return "/pages/productReviews3"; // 返回的頁面路徑
	}

	@GetMapping("/api/reviews/product/{productId}")
	@ResponseBody
	public ResponseEntity<Page<ReviewsDto4>> getReviewsApi(@PathVariable Integer productId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "2") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<ReviewsDto4> reviewsPage = reviewsService.getReviewsDto4ByProductId(productId, pageable);
		return ResponseEntity.ok(reviewsPage);
	}

	// 獲取平均評分API
	@ResponseBody
	@GetMapping("/reviews/product/{productId}/average-rating")
	public ResponseEntity<Double> getAverageRating(@PathVariable Integer productId) {
		Double average = reviewsRepository.findAverageRatingByProductId(productId);
		return ResponseEntity.ok(average != null ? average : 0.0);
	}

	// 修改後的篩選API
	// 修改篩選 API
	@GetMapping("/product/{productId}/filter")
	@ResponseBody
	public ResponseEntity<Page<ReviewsDto4>> getReviewsByRating(@PathVariable Integer productId,
			@RequestParam(required = false) Integer rating, @PageableDefault(size = 2) Pageable pageable) {

		Page<ReviewsDto> originalPage = reviewsRepository.findByProductIdAndRating(productId, rating, pageable);

		// 轉換為 ReviewsDto4
		Page<ReviewsDto4> convertedPage = originalPage.map(reviewDto -> {
			ReviewsDto4 dto4 = new ReviewsDto4();
			dto4.setUsername(reviewDto.getUsername());
			dto4.setRating(reviewDto.getRating());
			dto4.setComment(reviewDto.getComment());
			dto4.setUpdatedAt(reviewDto.getUpdatedAt());

			if (reviewDto.getPhoto() != null && reviewDto.getPhoto().length > 0) {
				String base64Photo = Base64.getEncoder().encodeToString(reviewDto.getPhoto());
				dto4.setPhoto("data:image/jpeg;base64," + base64Photo);
			} else {
				dto4.setPhoto(null);
			}

			return dto4;
		});

		return ResponseEntity.ok(convertedPage);
	}

	@GetMapping("/createreviews/{productId}")
	public String createReviews(@PathVariable Integer productId, Model m,
			@SessionAttribute(value = "user", required = false) Users user) {
		logger.info("Attempting to load template: pages/creatreviews");
		m.addAttribute("productId", productId);
		if (user == null) {
			m.addAttribute("productId", productId);
			return "/pages/customizedErrorPages/512";
		} else if (reviewsService.alreadyWrittenACommit(user.getUserId(), productId)) {
			m.addAttribute("productId", productId);
			return "/pages/customizedErrorPages/513";
		} else if (reviewsService.haveNotReceivedTheProductYet(user.getUserId(), productId)) {
			m.addAttribute("productId", productId);
			return "/pages/customizedErrorPages/514";
		}
		m.addAttribute("ProductName", productsRepository.findById(productId).map(Products::getProductName) // 如果存在就轉換成
																											// productName
				.orElse("尚未命名"));
		return "/pages/creatreviews2";
	}

	@PostMapping("/create.do")
	public String createReviewAndGoToProductReviews(@RequestParam("products.productId") Integer productId,
			@RequestParam("rating") Integer rating, @RequestParam(value = "comment", required = false) String comment,
			@RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
			@SessionAttribute(value = "user", required = false) Users user, HttpServletRequest request, Model model) {

		// 檢查登入狀態
		if (user == null) {
			model.addAttribute("showLoginModal", true);
			model.addAttribute("productId", productId);
			System.out.println("showLoginModal 被設定為 true"); // Debug 訊息
			return "/pages/creatreviews";
		}
		System.out.println("使用者ID為:" + user.getUserId());
		try {
			Reviews review = new Reviews();
			Products product = new Products();
			product.setProductId(productId);
			review.setProducts(product);
			review.setRating(rating);
			review.setComment(comment);

			// 處理圖片上傳
			if (photoFile != null && !photoFile.isEmpty()) {
				if (photoFile.getSize() > 5 * 1024 * 1024) {
					model.addAttribute("error", "圖片大小不能超過5MB");
					return "/pages/creatreviews";
				}
				try {
					review.setPhoto(photoFile.getBytes());
				} catch (IOException e) {
					logger.error("圖片讀取失敗", e);
					model.addAttribute("error", "圖片上傳失敗，請稍後再試");
					return "/pages/creatreviews";
				}
			}

			// 使用 userId 建立評論
			reviewsService.createReview(review, user.getUserId());
			// 重定向到產品評論頁面
			return "redirect:/reviews/product/" + productId;
		} catch (RuntimeException e) {
			model.addAttribute("error", e.getMessage());
			return "/pages/creatreviews";
		}
	}

	@GetMapping("/user/{userId}")
	public String getUserReviews(@PathVariable Integer userId, @PageableDefault(size = 3, page = 0) Pageable pageable,
			@SessionAttribute(value = "user", required = false) Users user, Model model) {// 檢查是否登入
		if (user == null) {
			return "/pages/customizedErrorPages/515"; // 如果未登入，導向自定義錯誤頁面515
		}

		// 確認訪問的 userId 是否是當前登入的用戶 ID
		if (!user.getUserId().equals(userId)) {
			return "/pages/customizedErrorPages/516"; // 如果訪問的 userId 不匹配，導向自定義錯誤頁面516
		}

		// 如果 userId 正確，則顯示該用戶的評價頁面
		model.addAttribute("userId", userId);
//		Page<ReviewsDto2> reviews = reviewsService.findUserReviews(userId, pageable);
//		model.addAttribute("reviews", reviews);
		return "/pages/userReviews"; // Thymeleaf 模板路徑
	}

	@GetMapping("/api/reviews/user/{userId}")
	@ResponseBody
	public ResponseEntity<Page<ReviewsDto5>> getReviewsApiByUser(@PathVariable Integer userId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "2") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<ReviewsDto5> reviewsPage = reviewsService.getReviewsDto5ByUserId(userId, pageable);
		return ResponseEntity.ok(reviewsPage);
	}

	// 新增編輯頁面
	@GetMapping("/edit/{reviewId}")
	public String editReview(@PathVariable Integer reviewId, @SessionAttribute("user") Users user, Model model) {

		Reviews review = reviewsRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("評論不存在"));

		// 權限驗證
		if (!review.getUsers().getUserId().equals(user.getUserId())) {
			return "/pages/customizedErrorPages/516";
		}

		model.addAttribute("review", review);
		model.addAttribute("productId", review.getProducts().getProductId());
		return "/pages/editReview"; // 使用類似 creatreviews2.html 的模板
	}

	@DeleteMapping("/{reviewId}")
	@ResponseBody
	public ResponseEntity<?> deleteReview(@PathVariable Integer reviewId, @SessionAttribute("user") Users user) {

		Reviews review = reviewsRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("評論不存在"));

		// 權限驗證
		if (!review.getUsers().getUserId().equals(user.getUserId())) {
			return ResponseEntity.status(403).body(Map.of("success", false, "message", "無權限操作"));
		}

		reviewsRepository.delete(review);
		return ResponseEntity.ok(Map.of("success", true));
	}

	@PostMapping("/update/{reviewId}")
	public String updateReview(@PathVariable Integer reviewId, @RequestParam Integer rating,
			@RequestParam(required = false) String comment, @RequestParam(required = false) MultipartFile photoFile,
			@RequestParam(required = false) Boolean deletePhoto, @SessionAttribute("user") Users user, Model model) {

		Reviews review = reviewsRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("評論不存在"));

		// 權限驗證
		if (!review.getUsers().getUserId().equals(user.getUserId())) {
			return "/pages/customizedErrorPages/516";
		}

		// 更新邏輯
		review.setRating(rating);
		review.setComment(comment);

		// 處理圖片刪除
		if (deletePhoto != null && deletePhoto) {
			review.setPhoto(null);
		}

		// 處理圖片上傳
		if (photoFile != null && !photoFile.isEmpty()) {
			if (photoFile.getSize() > 5 * 1024 * 1024) {
				model.addAttribute("error", "圖片大小不能超過5MB");
				return "redirect:/reviews/edit/" + reviewId;
			}
			try {
				review.setPhoto(photoFile.getBytes());
			} catch (IOException e) {
				logger.error("圖片讀取失敗", e);
				model.addAttribute("error", "圖片上傳失敗，請稍後再試");
				return "redirect:/reviews/edit/" + reviewId;
			}
		}

		reviewsRepository.save(review);
		return "redirect:/reviews/user/" + user.getUserId();
	}

	@GetMapping("/manage")
	public String manageAllReviews(@SessionAttribute(value = "user", required = false) Users user, Model model) {
		// 檢查是否登入
		if (user == null) {
			return "/pages/customizedErrorPages/515"; // 如果未登入，導向自定義錯誤頁面515
		}

		// 確認訪問的 userId 是否是當前登入的用戶 ID
		if (user.getUserId() != 99999999) {
			return "/pages/customizedErrorPages/516"; // 如果 userId 不為 99999999，則導向自定義錯誤頁面516
		}

		return "/pages/manageAllReviews";
	}

	@GetMapping("/api/reviews/manageAll")
	@ResponseBody
	public ResponseEntity<Page<ReviewsDto6>> manageAllReviewsApi(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(required = false) String comment, @SessionAttribute(value = "user", required = false) Users user) {

		logger.info("Received request to manageAllReviews with page={}, size={}, comment={}", page, size, comment);
	    
	    if (user == null || user.getUserId() != 99999999) {
	        logger.warn("Unauthorized access attempt by user: {}", user != null ? user.getUserId() : "null");}
		
		Pageable pageable = PageRequest.of(page, size);
		if (comment != null && comment.length() > 0) {
			Page<ReviewsDto6> reviewsPage = reviewsService.getReviewsDto6WithUserAndProductInfo(comment, pageable);
			return ResponseEntity.ok(reviewsPage);
		} else {
			Page<ReviewsDto6> reviewsPage = reviewsService.getReviewsDto6WithUserAndProductInfo(null, pageable);
			return ResponseEntity.ok(reviewsPage);
		}
	}
	
	// 新增批量刪除API
	@DeleteMapping("/api/reviews/deleteAll")
	@ResponseBody
	public ResponseEntity<String> deleteReviews(@RequestBody List<Integer> reviewIds) {
	    try {
	        reviewsService.deleteReviews(reviewIds);
	        return ResponseEntity.ok("刪除成功");
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body("刪除失敗: " + e.getMessage());
	    }
	}

//	@GetMapping("/api/reviews/manageSpecificReviewsByComment")
//	@ResponseBody
//	public ResponseEntity<Page<ReviewsDto6>> manageSpecificReviewsByCommentApi(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
//
//		Pageable pageable = PageRequest.of(page, size);
//		Page<ReviewsDto6> reviewsPage = reviewsService.getReviewsDto6WithUserAndProductInfo(null, pageable);
//		return ResponseEntity.ok(reviewsPage);
//	}

	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

}