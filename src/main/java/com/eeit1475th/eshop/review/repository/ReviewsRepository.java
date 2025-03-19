package com.eeit1475th.eshop.review.repository;

import com.eeit1475th.eshop.product.entity.Products;
import com.eeit1475th.eshop.review.entity.Reviews;
import com.eeit1475th.eshop.member.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Integer> {

    // 根據 reviewId 查詢單筆 Reviews
    Reviews findByReviewId(Integer reviewId);

    // 根據 reviews_product_id 查詢 Reviews，依 updated_at 由新到舊排序，並支援分頁
    Page<Reviews> findByProductOrderByUpdatedAtDesc(Products product, Pageable pageable);

    // 根據 reviews_product_id 和 rating 查詢 Reviews，依 updated_at 由新到舊排序，並支援分頁
    //rating 必須是 1~5 之間的正整數（這個約束可以在 Controller 層驗證）。
    Page<Reviews> findByProductAndRatingOrderByUpdatedAtDesc(Products product, Integer rating, Pageable pageable);

    // 根據 reviews_user_id 查詢 Reviews，依 updated_at 由新到舊排序，並支援分頁
    Page<Reviews> findByUserOrderByUpdatedAtDesc(Users user, Pageable pageable);
}