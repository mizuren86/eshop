package com.eeit1475th.eshop.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eeit1475th.eshop.cart.entity.Orders;
import com.eeit1475th.eshop.cart.entity.ShippingStatus;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {

	List<Orders> findByUsers_UserId(Integer userId);
	
	boolean existsByUsersUserIdAndOrderItemsProductsProductIdAndShippingStatus(Integer userId, Integer productId, ShippingStatus shippingStatus);
	
	@Query("SELECT o FROM Orders o LEFT JOIN FETCH o.orderItems WHERE o.orderId = :orderId")
	Orders findByIdWithItems(@Param("orderId") Integer orderId);
	
	Page<Orders> findByUsers_UserId(Integer userId, Pageable pageable);
	
	Optional<Orders> findByMerchantTradeNo(String merchantTradeNo);

}
