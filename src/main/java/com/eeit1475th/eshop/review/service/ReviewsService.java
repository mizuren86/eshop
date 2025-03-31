package com.eeit1475th.eshop.review.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eeit1475th.eshop.cart.entity.ShippingStatus;
import com.eeit1475th.eshop.cart.repository.OrdersRepository;
import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.member.repository.UsersRepository;
import com.eeit1475th.eshop.product.entity.Products;
import com.eeit1475th.eshop.product.repository.ProductsRepository;
import com.eeit1475th.eshop.review.dto.ReviewsDto;
import com.eeit1475th.eshop.review.entity.Reviews;
import com.eeit1475th.eshop.review.repository.ReviewsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewsService {

    private final ReviewsRepository reviewsRepository;
    private final OrdersRepository ordersRepository;
    private final UsersRepository usersRepository;
    private final ProductsRepository productsRepository;

    /**
     * 新增評論 (修改為使用 userId)
     */
    @Transactional
    public Reviews createReview(Reviews review, Integer userId) {
        if (userId == null) {
            throw new RuntimeException("需要先登入才能發表評論");
        }
        
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("使用者不存在，請先登入"));
        
        // 檢查該使用者是否已對該商品發表評論
        Optional<Reviews> existingReview = reviewsRepository.findByUsers_UserIdAndProducts_ProductId(
                userId, review.getProducts().getProductId());
        if (existingReview.isPresent()) {
            throw new RuntimeException("您已發表過評論");
        }
        
        // 檢查該使用者是否有購買該商品且訂單狀態為 DELIVERED
        boolean purchased = ordersRepository.existsByUsersUserIdAndOrderItemsProductsProductIdAndShippingStatus(
                userId, review.getProducts().getProductId(), ShippingStatus.Delivered);
        if (!purchased) {
            throw new RuntimeException("購買過這個商品且已送達才能發表評論");
        }
        
        review.setUsers(user);
        return reviewsRepository.save(review);
    }

    /**
     * 編輯評論 (修改為使用 userId)
     */
    @Transactional
    public Reviews updateReview(Integer reviewId, Reviews reviewUpdate, Integer userId) {
        if (userId == null) {
            throw new RuntimeException("需要先登入才能編輯評論");
        }
        
        Reviews existingReview = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("找不到評論"));

        if (!existingReview.getUsers().getUserId().equals(userId)) {
            throw new RuntimeException("您沒有權限編輯此評論");
        }
        
        existingReview.setRating(reviewUpdate.getRating());
        existingReview.setComment(reviewUpdate.getComment());
        existingReview.setPhoto(reviewUpdate.getPhoto());
        
        return reviewsRepository.save(existingReview);
    }

    /**
     * 刪除評論 (修改為使用 userId)
     */
    @Transactional
    public void deleteReview(Integer reviewId, Integer userId) {
        if (userId == null) {
            throw new RuntimeException("需要先登入才能刪除評論");
        }
        
        Reviews existingReview = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("找不到評論"));
        
        if (!existingReview.getUsers().getUserId().equals(userId)) {
            throw new RuntimeException("您沒有權限刪除此評論");
        }
        
        reviewsRepository.delete(existingReview);
    }
    
   

    
    @Transactional(readOnly = true)
    public Page<ReviewsDto> getReviewsByProductId(Integer productId, Pageable pageable) {
        // 檢查產品是否存在
        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("找不到該商品"));
        
        // 透過 repository 查詢該商品的評論
        return reviewsRepository.findByProductsOrderByUpdatedAtDesc(productId, pageable);
    }
}
