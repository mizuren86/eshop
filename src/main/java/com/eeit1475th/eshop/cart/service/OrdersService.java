package com.eeit1475th.eshop.cart.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eeit1475th.eshop.cart.dto.OrdersDTO;
import com.eeit1475th.eshop.cart.entity.CartItems;
import com.eeit1475th.eshop.cart.entity.OrderItems;
import com.eeit1475th.eshop.cart.entity.Orders;
import com.eeit1475th.eshop.cart.entity.PaymentStatus;
import com.eeit1475th.eshop.cart.entity.ShippingStatus;
import com.eeit1475th.eshop.cart.entity.ShoppingCart;
import com.eeit1475th.eshop.cart.repository.OrderItemsRepository;
import com.eeit1475th.eshop.cart.repository.OrdersRepository;
import com.eeit1475th.eshop.cart.repository.ShoppingCartRepository;

@Service
public class OrdersService {
	
	@Autowired
    private OrdersRepository ordersRepository;
    
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    
    @Autowired
    private OrderItemsRepository orderItemsRepository;
	
 // 檢查 ec_pay_trade_no 是否唯一
    public boolean isTradeNoUnique(String tradeNo) {
        return ordersRepository.findByEcPayTradeNo(tradeNo).isEmpty();
    }
    
 // 創建訂單
    public void createOrder(Integer userId, String ecPayTradeNo) {
        // 獲取用戶的購物車
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsers_UserId(userId);

        if (shoppingCart == null || shoppingCart.getCartItems().isEmpty()) {
            throw new RuntimeException("購物車為空，無法創建訂單");
        }
        
        // 如果 ec_pay_trade_no 不為空，則檢查唯一性
        if (ecPayTradeNo != null && !isTradeNoUnique(ecPayTradeNo)) {
            throw new RuntimeException("ec_pay_trade_no 已存在，請勿重複提交");
        }

        // 計算總金額
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItems cartItem : shoppingCart.getCartItems()) {
            totalAmount = totalAmount.add(cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
        }

        // 創建訂單
        Orders order = new Orders();
        order.setUsers(shoppingCart.getUsers());
        order.setTotalAmount(totalAmount);
        order.setPaymentStatus(PaymentStatus.Pending);  // 初始狀態為待支付
        order.setShippingStatus(ShippingStatus.Processing);  // 初始狀態為待發貨
        order.setEcPayTradeNo(ecPayTradeNo); // 設定交易編號（可以為 null）
        ordersRepository.save(order);

        // 將購物車中的商品轉換為訂單項目
        for (CartItems cartItem : shoppingCart.getCartItems()) {
            OrderItems orderItem = new OrderItems();
            orderItem.setOrders(order);
            orderItem.setProducts(cartItem.getProducts());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItemsRepository.save(orderItem);
        }

        // 清空購物車
        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
    }

    // 查詢訂單詳情
    public OrdersDTO getOrderDetails(Integer orderId) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("訂單未找到"));
        
        return new OrdersDTO(
                order.getOrderId(),
                order.getUsers().getUserId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getPaymentStatus().name()
        );
    }

    // 更新訂單狀態（付款、發貨等）
    public void updateOrderStatus(Integer orderId, String status) {
        Orders order = ordersRepository.findById(orderId).orElseThrow(() -> new RuntimeException("訂單未找到"));
        
        // 更新訂單狀態邏輯
        if ("paid".equalsIgnoreCase(status)) {
            order.setPaymentStatus(PaymentStatus.Paid);
        } else if ("shipped".equalsIgnoreCase(status)) {
            order.setShippingStatus(ShippingStatus.Shipped);
        }
        
        ordersRepository.save(order);
    }
    
    public List<OrdersDTO> getOrdersByUser(Integer userId) {
    	// 從資料庫查詢該使用者的所有訂單
        List<Orders> orders = ordersRepository.findByUsers_UserId(userId);
        
     // 將每筆 Orders 實體轉換成 OrdersDTO
        return orders.stream()
                .map(order -> new OrdersDTO(
                        order.getOrderId(),
                        order.getUsers().getUserId(),   // 假設 Orders 中使用者的 getter 為 getUsers()
                        order.getOrderDate(),
                        order.getTotalAmount(),
                        order.getPaymentStatus().toString()  // 若 paymentStatus 為 Enum，可轉換成 String
                    ))
                .collect(Collectors.toList());
    }

}
