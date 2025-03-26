package com.eeit1475th.eshop.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eeit1475th.eshop.review.entity.Reviews;
import com.eeit1475th.eshop.review.service.ReviewsService;

import lombok.RequiredArgsConstructor;

//@RestController
//@RequestMapping("/reviews")
//@RequiredArgsConstructor
public class ReviewsRestController {

//	private final ReviewsService reviewsService;

//	@GetMapping("/product/{productId}")
//	public ResponseEntity<Page<Reviews>> getReviewsByProductId(@PathVariable Integer productId,
//			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
//
//		Pageable pageable = PageRequest.of(page, size);
//		Page<Reviews> reviewsPage = reviewsService.getReviewsByProductId(productId, pageable);
//
//		return ResponseEntity.ok(reviewsPage);
//	}
}
//    @GetMapping("/product/{productId}")
//    public Page<Reviews> getReviewsByProduct(@PathVariable Integer productId, Pageable pageable) {
//        return reviewsService.getReviewsByProductId(productId, pageable);
//    }
// 已經用POSTMAN測試過，可以GET
// http://localhost:8080/reviews/product/{productId}?page=0&size=10，來獲取該產品的評論，並支援分頁功能。
// 但還需要修改網址，目標是在商品頁面可以加載出該商品的評論

// merge test
