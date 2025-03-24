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
public class ReviewsDto {
    private String username;
    private String userPhoto;
    private Integer rating;
    private String comment;
    private byte[] photo;
    private LocalDateTime updatedAt;

    
}