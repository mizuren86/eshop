package com.eeit1475th.eshop.review.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//將ReviewsDto的photo轉為Base64字串
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewsDto4 {
	private String username;
	private Integer rating;
	private String comment;
	private String photo;
	private LocalDateTime updatedAt;
}
