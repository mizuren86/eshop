package com.eeit1475th.eshop.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eeit1475th.eshop.review.dto.ReviewsDto;
import com.eeit1475th.eshop.review.service.ReviewsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewsService reviewsService;

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
        
        return "/pages/productReviews2"; // 返回的頁面路徑
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
}
