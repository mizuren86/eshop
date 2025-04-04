package com.eeit1475th.eshop.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.eeit1475th.eshop.product.entity.ProductCategory;

@CrossOrigin("http://localhost:4200")
@RepositoryRestResource(collectionResourceRel = "productCategory", path = "product-category")
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {

	// 根據分類名稱模糊搜尋（不區分大小寫）
	List<ProductCategory> findByCategoryNameContainingIgnoreCase(String categoryName);

}