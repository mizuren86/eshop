package com.eeit1475th.eshop.review.service;

import java.util.Base64;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eeit1475th.eshop.cart.entity.ShippingStatus;
import com.eeit1475th.eshop.cart.repository.OrdersRepository;
import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.member.repository.UsersRepository;
import com.eeit1475th.eshop.review.dto.ReviewsDto;
import com.eeit1475th.eshop.review.dto.ReviewsDto2;
import com.eeit1475th.eshop.review.dto.ReviewsDto4;
import com.eeit1475th.eshop.review.dto.ReviewsDto5;
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

	/**
	 * 新增評論 (修改為使用 userId)
	 */
	@Transactional
	public Reviews createReview(Reviews review, Integer userId) {
//        if (userId == null) {
//            throw new RuntimeException("需要先登入才能發表評論");
//        }
//        
		Users user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("使用者不存在，請先登入"));

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

		Reviews existingReview = reviewsRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("找不到評論"));

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

		Reviews existingReview = reviewsRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("找不到評論"));

		if (!existingReview.getUsers().getUserId().equals(userId)) {
			throw new RuntimeException("您沒有權限刪除此評論");
		}

		reviewsRepository.delete(existingReview);
	}

	@Transactional(readOnly = true)
	public Page<ReviewsDto> getReviewsByProductId(Integer productId, Pageable pageable) {

		// 透過 repository 查詢該商品的評論
		return reviewsRepository.findByProductsOrderByUpdatedAtDesc(productId, pageable);
	}

	// 將ReviewsDto轉為ReviewsDto4
	@Transactional(readOnly = true)
	public Page<ReviewsDto4> getReviewsDto4ByProductId(Integer productId, Pageable pageable) {
		// 取得原始 ReviewsDto Page
		Page<ReviewsDto> originalPage = reviewsRepository.findByProductsOrderByUpdatedAtDesc(productId, pageable);

		// 轉換為 ReviewsDto4 Page
		return originalPage.map(reviewDto -> {
			ReviewsDto4 dto4 = new ReviewsDto4();
			dto4.setUsername(reviewDto.getUsername());
			dto4.setRating(reviewDto.getRating());
			dto4.setComment(reviewDto.getComment());
			dto4.setUpdatedAt(reviewDto.getUpdatedAt());

			// 將 byte[] 轉為 Base64 字串 (僅處理非空數據)
			if (reviewDto.getPhoto() != null && reviewDto.getPhoto().length > 0) {
				String base64Photo = Base64.getEncoder().encodeToString(reviewDto.getPhoto());
				dto4.setPhoto("data:image/jpeg;base64," + base64Photo); // 補上 MIME 類型
			} else {
				dto4.setPhoto(null);
			}

			return dto4;
		});
	}

	public boolean alreadyWrittenACommit(Integer userId, Integer productId) {
		// 檢查該使用者是否已對該商品發表評論
		Optional<Reviews> existingReview = reviewsRepository.findByUsers_UserIdAndProducts_ProductId(userId, productId);
		if (existingReview.isPresent()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean haveNotReceivedTheProductYet(Integer userId, Integer productId) {

		// 檢查該使用者是否有購買該商品且訂單狀態為 DELIVERED
		boolean purchased = ordersRepository.existsByUsersUserIdAndOrderItemsProductsProductIdAndShippingStatus(userId,
				productId, ShippingStatus.Delivered);
		if (!purchased) {
			return true;
		} else {
			return false;
		}
	}

	public Page<ReviewsDto2> findUserReviews(Integer userId, Pageable pageable) {
		return reviewsRepository.findReviewsByUserIdWithProductInfo(userId, pageable);
	}
	
	public Page<ReviewsDto5> getReviewsDto5ByUserId(Integer userId, Pageable pageable) {
		// 取得原始 ReviewsDto Page
		Page<ReviewsDto2> originalPage = reviewsRepository.findReviewsByUserIdWithProductInfo(userId, pageable);

		// 轉換為 ReviewsDto5 Page
		return originalPage.map(reviewDto2 -> {
			ReviewsDto5 dto5 = new ReviewsDto5();
			dto5.setReviewId(reviewDto2.getReviewId());
			dto5.setProductId(reviewDto2.getProductId());
			dto5.setProductName(reviewDto2.getProductName());
			dto5.setRating(reviewDto2.getRating());
			dto5.setComment(reviewDto2.getComment());
			dto5.setUpdatedAt(reviewDto2.getUpdatedAt());

			// 將 byte[] 轉為 Base64 字串 (僅處理非空數據)
			if (reviewDto2.getPhoto() != null && reviewDto2.getPhoto().length > 0) {
				String base64Photo = Base64.getEncoder().encodeToString(reviewDto2.getPhoto());
				dto5.setPhoto("data:image/jpeg;base64," + base64Photo); // 補上 MIME 類型
			} else {
				dto5.setPhoto(null);
			}

			return dto5;
		});
	}
	
	// 新增照片刪除邏輯
	@Transactional
	public void updateReviewPhoto(Integer reviewId, byte[] photo) {
	    Reviews review = reviewsRepository.findById(reviewId)
	        .orElseThrow(() -> new RuntimeException("評論不存在"));
	    review.setPhoto(photo);
	    reviewsRepository.save(review);
	}

	@Transactional
	public void deleteReviewPhoto(Integer reviewId) {
	    Reviews review = reviewsRepository.findById(reviewId)
	        .orElseThrow(() -> new RuntimeException("評論不存在"));
	    review.setPhoto(null);
	    reviewsRepository.save(review);
	}
}
