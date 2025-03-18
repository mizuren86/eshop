package com.eeit1475th.eshop.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.eeit1475th.eshop.product.entity.Products;

@CrossOrigin("http://localhost:4200")
public interface ProductsRepository extends JpaRepository<Products, Integer> {

//	Page<Products> findByCategoryId(@Param("id") Integer id, Pageable pageable);

//	Page<Products> findByNameContaining(@Param("name") String name, Pageable page);
	
	Page<Products> findByProductCategoryCategoryId(@Param("id") Integer id, Pageable pageable);
	
	Page<Products> findByProductNameContaining(@Param("name") String name, Pageable page);

	
}
