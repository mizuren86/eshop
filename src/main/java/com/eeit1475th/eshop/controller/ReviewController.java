package com.eeit1475th.eshop.controller;

import java.util.HashMap;
import java.util.Map;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eeit1475th.eshop.review.dto.ReviewsDto;
import com.eeit1475th.eshop.review.entity.Reviews;
import com.eeit1475th.eshop.review.repository.ReviewsRepository;
import com.eeit1475th.eshop.review.service.ReviewsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewsService reviewsService;
    private final ReviewsRepository reviewsRepository;
    

    @GetMapping("/product/{productId}")
    public String getReviewsByProductId(@PathVariable Integer productId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "3") int size,
                                         Model model) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<ReviewsDto> reviewsPage = reviewsService.getReviewsByProductId(productId, pageable);
//        
//        model.addAttribute("reviews", reviewsPage);
//        model.addAttribute("productId", productId); // 可以用於前端顯示或其他功能
        
        return "/pages/productReviews3"; // 返回的頁面路徑
    }
    
    @GetMapping("/api/reviews/product/{productId}")
    @ResponseBody
    public ResponseEntity<Page<ReviewsDto>> getReviewsApi(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reviewsService.getReviewsByProductId(productId, pageable));
    }
    
 // 獲取平均評分API
    @ResponseBody
    @GetMapping("/reviews/product/{productId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Integer productId) {
        Double average = reviewsRepository.findAverageRatingByProductId(productId);
        return ResponseEntity.ok(average != null ? average : 0.0);
    }
 // 修改後的篩選API
    @GetMapping("/product/{productId}/filter")
    @ResponseBody
    public ResponseEntity<Page<ReviewsDto>> getReviewsByRating(
            @PathVariable Integer productId,
            @RequestParam(required = false) Integer rating,
            @PageableDefault(size = 1) Pageable pageable) {
        
    	
        // 這裡不需要再額外添加排序
        Page<ReviewsDto> reviews = reviewsRepository.findByProductIdAndRating(
            productId, 
            rating, 
            pageable
        );        
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/createreviews/{productId}")
    public String createReviews(@PathVariable Integer productId, Model m) {
    	logger.info("Attempting to load template: pages/creatreviews");
        m.addAttribute("productId", productId);
        return "/pages/creatreviews";
    }
    
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createReview(
            @RequestBody Reviews review,
            @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Reviews createdReview = reviewsService.createReview(review, token);
            response.put("success", true);
            response.put("review", createdReview);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/update/{reviewId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateReview(
            @PathVariable Integer reviewId,
            @RequestBody Reviews reviewUpdate,
            @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            Reviews updatedReview = reviewsService.updateReview(reviewId, reviewUpdate, token);
            response.put("success", true);
            response.put("review", updatedReview);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/delete/{reviewId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteReview(
            @PathVariable Integer reviewId,
            @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            reviewsService.deleteReview(reviewId, token);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

}
