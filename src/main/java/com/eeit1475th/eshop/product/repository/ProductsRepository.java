package com.eeit1475th.eshop.product.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.eeit1475th.eshop.product.entity.Products;

@CrossOrigin("http://localhost:4200")
public interface ProductsRepository extends JpaRepository<Products, Integer> {

	// 根據分類 ID 查詢商品（支援分頁）
	Page<Products> findByProductCategoryCategoryId(@Param("id") Integer id, Pageable pageable);

	// 根據商品名稱模糊搜尋（支援分頁）
	Page<Products> findByProductNameContaining(@Param("name") String name, Pageable pageable);

	// 依商品名稱模糊搜尋（不區分大小寫）
	List<Products> findByProductNameContainingIgnoreCase(@Param("name") String name);

}
