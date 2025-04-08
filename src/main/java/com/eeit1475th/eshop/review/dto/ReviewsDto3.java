package com.eeit1475th.eshop.review.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReviewsDto3 {
	private String userName;
	private String productName;
	private Integer reviewId;
	private Integer userId;
	private Integer productId;
	private Integer rating;
	private String comment;
	private byte[] photo;
	private LocalDateTime updatedAt;
}
