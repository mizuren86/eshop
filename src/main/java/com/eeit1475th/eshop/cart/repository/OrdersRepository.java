package com.eeit1475th.eshop.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eeit1475th.eshop.cart.entity.Orders;
import com.eeit1475th.eshop.cart.entity.PaymentStatus;
import com.eeit1475th.eshop.cart.entity.ShippingStatus;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {

	List<Orders> findByUsers_UserId(Integer userId);

	boolean existsByUsersUserIdAndOrderItemsProductsProductIdAndShippingStatus(Integer userId, Integer productId,
			ShippingStatus shippingStatus);

	@Query("SELECT o FROM Orders o LEFT JOIN FETCH o.orderItems WHERE o.orderId = :orderId")
	Orders findByIdWithItems(@Param("orderId") Integer orderId);

	Page<Orders> findByUsers_UserId(Integer userId, Pageable pageable);

	Optional<Orders> findByMerchantTradeNo(String merchantTradeNo);

	// 使用 @Query 實現不包含 orderId 條件的搜尋（付款、配送狀態採 Enum 精確比對）
	@Query("SELECT o FROM Orders o "
			+ "WHERE (:merchantTradeNo IS NULL OR o.merchantTradeNo LIKE CONCAT('%', :merchantTradeNo, '%')) "
			+ "AND (:paymentStatusEnum IS NULL OR o.paymentStatus = :paymentStatusEnum) "
			+ "AND (:shippingStatusEnum IS NULL OR o.shippingStatus = :shippingStatusEnum)")
	Page<Orders> searchOrders(@Param("merchantTradeNo") String merchantTradeNo,
			@Param("paymentStatusEnum") PaymentStatus paymentStatusEnum,
			@Param("shippingStatusEnum") ShippingStatus shippingStatusEnum, Pageable pageable);

	// 使用 @Query 實現包含 orderId 精確匹配的搜尋，其餘條件同上
	@Query("SELECT o FROM Orders o " + "WHERE (:orderId IS NULL OR o.orderId = :orderId) "
			+ "AND (:merchantTradeNo IS NULL OR o.merchantTradeNo LIKE CONCAT('%', :merchantTradeNo, '%')) "
			+ "AND (:paymentStatusEnum IS NULL OR o.paymentStatus = :paymentStatusEnum) "
			+ "AND (:shippingStatusEnum IS NULL OR o.shippingStatus = :shippingStatusEnum)")
	Page<Orders> searchOrdersWithOrderId(@Param("orderId") Integer orderId,
			@Param("merchantTradeNo") String merchantTradeNo,
			@Param("paymentStatusEnum") PaymentStatus paymentStatusEnum,
			@Param("shippingStatusEnum") ShippingStatus shippingStatusEnum, Pageable pageable);
}
