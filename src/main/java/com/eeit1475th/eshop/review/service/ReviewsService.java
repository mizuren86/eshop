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
import com.eeit1475th.eshop.member.service.JwtService;
import com.eeit1475th.eshop.product.entity.Products;
import com.eeit1475th.eshop.product.repository.ProductsRepository;
import com.eeit1475th.eshop.review.entity.Reviews;
import com.eeit1475th.eshop.review.repository.ReviewsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewsService {

    private final ReviewsRepository reviewsRepository;
    private final OrdersRepository ordersRepository;
    private final UsersRepository usersRepository; // 假設 UsersRepository 有依據 username 查詢使用者的方法
    private final ProductsRepository productsRepository;
    private final JwtService jwtService;

    /**
     * 新增評論前先檢查：
     * 1. 必須登入
     * 2. 此使用者是否已發表過該商品的評論
     * 3. 該商品是否曾被該使用者訂購且訂單狀態為 DELIVERED
     */
    @Transactional
    public Reviews createReview(Reviews review, String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("需要先登入才能發表評論");
        }
        String username = jwtService.extractUsername(token);
        if (username == null) {
            throw new RuntimeException("需要先登入才能發表評論");
        }
        // 透過 username 找到完整使用者資料
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在，請先登入"));
        
        // 檢查該使用者是否已對該商品發表評論
        Optional<Reviews> existingReview = reviewsRepository.findByUsers_UserIdAndProducts_ProductId(
                user.getUserId(), review.getProducts().getProductId());
        if (existingReview.isPresent()) {
            throw new RuntimeException("您已發表過評論");
        }
        
        // 檢查該使用者是否有購買該商品且訂單狀態為 DELIVERED
        boolean purchased = ordersRepository.existsByUsersUserIdAndOrderItemsProductsProductIdAndShippingStatus(
                user.getUserId(), review.getProducts().getProductId(), ShippingStatus.Delivered);
        if (!purchased) {
            throw new RuntimeException("購買過這個商品且已送達才能發表評論");
        }
        
        review.setUsers(user);
        return reviewsRepository.save(review);
    }

    /**
     * 編輯評論
     * 只有發表此評論的使用者才可編輯
     */
    @Transactional
    public Reviews updateReview(Integer reviewId, Reviews reviewUpdate, String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("需要先登入才能編輯評論");
        }
        String username = jwtService.extractUsername(token);
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在，請先登入"));
        
        Reviews existingReview = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("找不到評論"));

        if (!existingReview.getUsers().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("您沒有權限編輯此評論");
        }
        
        // 更新可編輯的欄位，例如 rating, comment, photo
        existingReview.setRating(reviewUpdate.getRating());
        existingReview.setComment(reviewUpdate.getComment());
        existingReview.setPhoto(reviewUpdate.getPhoto());
        
        return reviewsRepository.save(existingReview);
    }

    /**
     * 刪除評論
     * 只有發表此評論的使用者才可刪除
     */
    @Transactional
    public void deleteReview(Integer reviewId, String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("需要先登入才能刪除評論");
        }
        String username = jwtService.extractUsername(token);
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在，請先登入"));
        
        Reviews existingReview = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("找不到評論"));
        
        if (!existingReview.getUsers().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("您沒有權限刪除此評論");
        }
        
        reviewsRepository.delete(existingReview);
    }
    
    @Transactional(readOnly = true)
    public Page<Reviews> getReviewsByProductId(Integer productId, Pageable pageable) {
        // 檢查產品是否存在
        Products product = productsRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("找不到該商品"));
        
        // 透過 repository 查詢該商品的評論
        return reviewsRepository.findByProductsOrderByUpdatedAtDesc(product, pageable);
    }
}
