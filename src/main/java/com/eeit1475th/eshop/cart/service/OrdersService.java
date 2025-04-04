package com.eeit1475th.eshop.cart.service;

import java.math.BigDecimal;
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
    
    /**
     * 創建訂單（若需要單獨使用此方法，可與 CheckoutService 區分）
     * 此方法從使用者購物車中取得資料，計算總金額，建立訂單與訂單項目，
     * 並清空購物車。
     * 
     * @param userId 使用者編號
     * @param ecPayTradeNo 交易編號
     */
    public void createOrder(Integer userId, String ecPayTradeNo) {
        // 獲取使用者購物車
        ShoppingCart shoppingCart = shoppingCartRepository.findByUsers_UserId(userId);

        if (shoppingCart == null || shoppingCart.getCartItems().isEmpty()) {
            throw new RuntimeException("購物車為空，無法創建訂單");
        }
        
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
        order.setPaymentStatus(PaymentStatus.Pending);   // 初始狀態為待支付
        order.setShippingStatus(ShippingStatus.Processing); // 初始狀態為待發貨
        order.setEcPayTradeNo(ecPayTradeNo);
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

    /**
     * 查詢訂單詳情，將 Orders 與相關訂單項目轉換為 OrderDTO 回傳
     * 
     * @param orderId 訂單編號
     * @return OrderDTO 訂單資料
     */
    public OrderDTO getOrderDetails(Integer orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單未找到"));
        
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setPaymentStatus(order.getPaymentStatus().name());
        orderDTO.setShipmentStatus(order.getShippingStatus().name());
        
        // 轉換訂單項目
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

    /**
     * 更新訂單狀態（付款、發貨等）
     * 
     * @param orderId 訂單編號
     * @param status 要更新的狀態，例："paid" 或 "shipped"
     */
    public void updateOrderStatus(Integer orderId, String status) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單未找到"));
        
        if ("paid".equalsIgnoreCase(status)) {
            order.setPaymentStatus(PaymentStatus.Paid);
        } else if ("shipped".equalsIgnoreCase(status)) {
            order.setShippingStatus(ShippingStatus.Shipped);
        }
        
        ordersRepository.save(order);
    }
    
    /**
     * 取得指定使用者的所有訂單（不包含訂單明細，可視需求擴充）
     * 
     * @param userId 使用者編號
     * @return List<OrderDTO> 該使用者所有訂單
     */
    public List<OrderDTO> getOrdersByUser(Integer userId) {
        List<Orders> orders = ordersRepository.findByUsers_UserId(userId);
        
        return orders.stream().map(order -> {
            OrderDTO dto = new OrderDTO();
            dto.setOrderId(order.getOrderId());
            dto.setOrderDate(order.getOrderDate());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setPaymentStatus(order.getPaymentStatus().toString());
            dto.setShipmentStatus(order.getShippingStatus().toString());
            // 若需要，可進一步加入訂單明細
            return dto;
        }).collect(Collectors.toList());
    }
}
