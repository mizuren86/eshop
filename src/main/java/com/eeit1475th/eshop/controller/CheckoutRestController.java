package com.eeit1475th.eshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eeit1475th.eshop.cart.dto.OrderDTO;
import com.eeit1475th.eshop.cart.service.CheckoutService;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutRestController {

    @Autowired
    private CheckoutService checkoutService;

    /**
     * 處理結帳流程，從購物車產生訂單並返回訂單資料
     *
     * @param userId 使用者編號
     * @param ecPayTradeNo 交易編號（可為 null）
     * @return OrderDTO 訂單資料
     */
    @PostMapping("/process")
    public ResponseEntity<OrderDTO> processCheckout(
            @RequestParam("userId") Integer userId,
            @RequestParam(value = "ecPayTradeNo", required = false) String ecPayTradeNo) {

        OrderDTO orderDTO = checkoutService.processCheckout(userId, ecPayTradeNo);
        return ResponseEntity.ok(orderDTO);
    }
}
