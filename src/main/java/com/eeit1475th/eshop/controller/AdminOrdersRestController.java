package com.eeit1475th.eshop.controller;

import com.eeit1475th.eshop.cart.dto.OrderItemDTO;
import com.eeit1475th.eshop.cart.dto.OrderDTO;
import com.eeit1475th.eshop.cart.service.OrdersService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-orders")
public class AdminOrdersRestController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 根據傳入的訂單編號查詢訂單明細（訂單項目）。
     * 回傳的 JSON 陣列每筆資料包括 productId, productName, imageUrl, price, quantity, subTotal 等欄位。
     */
    @GetMapping("/orderDetails/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Integer orderId) {
        try {
            // 取得訂單資料（包含訂單項目）
            OrderDTO orderDTO = ordersService.getOrderDetails(orderId);
            List<OrderItemDTO> orderItems = orderDTO.getOrderItems();
            return ResponseEntity.ok(orderItems);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving order details: " + e.getMessage());
        }
    }
}
