package com.eeit1475th.eshop.cart.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eeit1475th.eshop.cart.dto.OrderDTO;
import com.eeit1475th.eshop.cart.dto.OrderItemDTO;
import com.eeit1475th.eshop.cart.entity.CartItems;
import com.eeit1475th.eshop.cart.entity.OrderItems;
import com.eeit1475th.eshop.cart.entity.Orders;
import com.eeit1475th.eshop.cart.entity.PaymentStatus;
import com.eeit1475th.eshop.cart.entity.ShippingStatus;
import com.eeit1475th.eshop.cart.entity.ShoppingCart;
import com.eeit1475th.eshop.cart.repository.OrderItemsRepository;
import com.eeit1475th.eshop.cart.repository.OrdersRepository;
import com.eeit1475th.eshop.cart.repository.ShoppingCartRepository;
import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.member.repository.UsersRepository;

@Service
public class CheckoutService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private UsersRepository userRepository;

    /**
     * 處理結帳流程：  
     * 1. 從購物車取得資料，計算總金額  
     * 2. 檢查 ec_pay_trade_no 的唯一性（若有傳入）  
     * 3. 建立訂單與訂單項目，清空購物車  
     * 4. 回傳封裝好的 OrderDTO
     * 
     * @param userId 使用者編號
     * @param ecPayTradeNo 交易編號，可為 null
     * @return OrderDTO 訂單資料
     */
    public OrderDTO processCheckout(Integer userId, String ecPayTradeNo) {
        // 取得使用者與購物車
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到使用者，userId: " + userId));
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsers_UserId(userId);
        if (shoppingCart == null || shoppingCart.getCartItems().isEmpty()) {
            throw new RuntimeException("購物車為空，無法創建訂單");
        }
        
        // 檢查 ec_pay_trade_no 是否唯一（若有傳入）
        if (ecPayTradeNo != null && !ordersRepository.findByEcPayTradeNo(ecPayTradeNo).isEmpty()) {
            throw new RuntimeException("ec_pay_trade_no 已存在，請勿重複提交");
        }
        
        // 計算總金額
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItems cartItem : shoppingCart.getCartItems()) {
            BigDecimal itemTotal = cartItem.getPrice().multiply(new BigDecimal(cartItem.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        
        // 建立訂單
        Orders order = new Orders();
        order.setUsers(user);
        order.setTotalAmount(totalAmount);
        order.setPaymentStatus(PaymentStatus.Pending);    // 初始為待支付
        order.setShippingStatus(ShippingStatus.Processing); // 初始為待出貨
        order.setEcPayTradeNo(ecPayTradeNo);
        order.setOrderDate(LocalDateTime.now());
        ordersRepository.save(order);

        // 將購物車中的每筆商品轉換為訂單項目
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
        
        // 將 Orders 與其 OrderItems 轉換為 OrderDTO
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setPaymentStatus(order.getPaymentStatus().name());
        orderDTO.setShipmentStatus(order.getShippingStatus().name());
        
        // 假設 Orders 實體中有 getOrderItems() 取得所有訂單項目
        List<OrderItemDTO> orderItemDTOList = order.getOrderItems().stream()
            .map(oi -> {
                OrderItemDTO dto = new OrderItemDTO();
                dto.setProductId(oi.getProducts().getProductId());
                dto.setProductName(oi.getProducts().getProductName());
                dto.setPrice(oi.getPrice());
                dto.setQuantity(oi.getQuantity());
                dto.setSubTotal(oi.getPrice().multiply(new BigDecimal(oi.getQuantity())));
                return dto;
            }).collect(Collectors.toList());
        orderDTO.setOrderItems(orderItemDTOList);
        
        return orderDTO;
    }
}
