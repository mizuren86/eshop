package com.eeit1475th.eshop.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.eeit1475th.eshop.review.entity.Reviews;
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
        Pageable pageable = PageRequest.of(page, size);
        Page<Reviews> reviewsPage = reviewsService.getReviewsByProductId(productId, pageable);
        
        model.addAttribute("reviews", reviewsPage);
        model.addAttribute("productId", productId); // 可以用於前端顯示或其他功能
        
        return "/pages/productReviews"; // 返回的頁面路徑
    }
}
