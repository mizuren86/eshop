package com.eeit1475th.eshop.review.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewsDto5 {
	Integer reviewId;          // 新增：為了後續可能需要的操作
    Integer productId;         // 必須攜帶！用於生成產品連結
    String productName;        // 產品名稱
    Integer rating;            // 評分
    String comment;            // 評論內容
    String photo;              // 評論照片
    LocalDateTime updatedAt;    // 更新時間
}
