package com.eeit1475th.eshop.review.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.product.entity.Products;
import com.eeit1475th.eshop.review.dto.ReviewsDto;
import com.eeit1475th.eshop.review.entity.Reviews;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Integer> {

    // 根據 reviewId 查詢單筆 Reviews
    Optional<Reviews> findByReviewId(Integer reviewId);
    
    // 根據 Reviews 的 Users 的 UserId 和 Products 的 ProductId 查詢單筆 Reviews
    Optional<Reviews> findByUsers_UserIdAndProducts_ProductId(Integer userId, Integer productId); 

    // 根據 reviews_product_id 查詢 Reviews，依 updated_at 由新到舊排序，並支援分頁
//    Page<Reviews> findByProductsOrderByUpdatedAtDesc(Products product, Pageable pageable); 

    // 根據 reviews_product_id 和 rating 查詢 Reviews，依 updated_at 由新到舊排序，並支援分頁
    Page<Reviews> findByProductsAndRatingOrderByUpdatedAtDesc(Products product, Integer rating, Pageable pageable); 

    // 根據 reviews_user_id 查詢 Reviews，依 updated_at 由新到舊排序，並支援分頁
    Page<Reviews> findByUsersOrderByUpdatedAtDesc(Users user, Pageable pageable); 
    //merge test
    
    @Query("SELECT r FROM Reviews r JOIN FETCH r.users JOIN FETCH r.products WHERE r.products = :product ORDER BY r.updatedAt DESC")
    Page<Reviews> findByProductsOrderByUpdatedAtDesc(@Param("product") Products product, Pageable pageable);
    
    @Query("SELECT new com.eeit1475th.eshop.review.dto.ReviewsDto(r.users.username, r.users.userPhoto, r.rating, r.comment, r.photo, r.updatedAt) " +
            "FROM Reviews r " +
            "JOIN r.products p " +
            "JOIN r.users u " +
            "WHERE p.productId = :productId " +
            "ORDER BY r.updatedAt DESC")
     Page<ReviewsDto> findByProductsOrderByUpdatedAtDesc(@Param("productId") Integer productId,
                                                         Pageable pageable);
}
