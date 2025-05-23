package com.eeit1475th.eshop.review.entity;

import java.time.LocalDateTime;
import java.util.Arrays;

import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.product.entity.Products;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews", uniqueConstraints = {
	    @UniqueConstraint(columnNames = {"reviews_user_id", "reviews_product_id"})
	})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reviews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @ManyToOne(fetch = FetchType.LAZY)  //使用 @ManyToOne 來對應 products 和 users 表的外鍵關係，讓 Spring Data JPA 自動管理這些關聯。
    @JoinColumn(name = "reviews_product_id", nullable = false)
    @JsonBackReference(value = "product-reviews")
    private Products products;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviews_user_id", nullable = false)
    @JsonBackReference(value = "user-reviews")
    private Users users;

    @Column(nullable = false)
    //@Min(1)  評分最小值 1
    //@Max(5)  評分最大值 5
    private Integer rating;

    //@Column(columnDefinition = "NVARCHAR(1000)")
    @Column(length = 1000)
    private String comment;

    @Lob
    @Column(name = "photo", columnDefinition = "VARBINARY(MAX)")
    private byte[] photo;

    @Column(name = "updated_at", nullable = false) //updated_at的預設值為LocalDateTime.now()，並在更新時自動變更。
    private LocalDateTime updatedAt = LocalDateTime.now();
    //更新評論時updated_at會自動更新為當前時間
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    //新增評論時自動設定updated_at
    @PrePersist
    public void prePersist() {
        this.updatedAt = LocalDateTime.now();
    }
	@Override
	public String toString() {
		return "Reviews [reviewId=" + reviewId + ", products=" + products + ", users=" + users + ", rating=" + rating
				+ ", comment=" + comment + ", photo=" + Arrays.toString(photo) + ", updatedAt=" + updatedAt + "]";
	}
    //這樣就不用手動在 Service 層或 Repository 層設定 updatedAt，JPA 會自動處理！
    
    
}
