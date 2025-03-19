package com.eeit1475th.eshop.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.eeit1475th.eshop.cart.dto.CartItemsDTO;
import com.eeit1475th.eshop.cart.service.ShoppingCartService;
import com.eeit1475th.eshop.member.entity.Users;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/cart")
public class ShoppingCartRestController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    // 取得購物車資料
    @GetMapping
//    public ResponseEntity<?> getCart(@SessionAttribute(value = "user", required = false) Users user) {
    public ResponseEntity<?> getCart(@SessionAttribute(value = "user", required = false) Users user,HttpSession session) {
//        if (user == null) {
//            return ResponseEntity.badRequest().body("未登入");
//        }
    	
        if (user == null) {
            // 暫時指定一個測試用會員（僅限於開發測試階段）
            user = new Users();
            user.setUserId(3); // 設定一個測試用的會員ID
        }
        
        List<CartItemsDTO> cartItems = shoppingCartService.getCartItems(user.getUserId());
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItemsDTO item : cartItems) {
            totalAmount = totalAmount.add(item.getSubTotal());
        }
        Map<String, Object> response = new HashMap<>();
        response.put("cartItems", cartItems);
        response.put("totalAmount", totalAmount);
        return ResponseEntity.ok(response);
    }

    // 移除購物車商品
    @PostMapping("/remove")
    public ResponseEntity<?> removeCartItem(@RequestParam("cartItemId") Integer cartItemId,
                                            @SessionAttribute(value = "user", required = false) Users user) {
        if (user == null) {
            return ResponseEntity.badRequest().body("未登入");
        }
        shoppingCartService.removeCartItem(cartItemId);
        return ResponseEntity.ok("移除成功");
    }

    // 更新購物車內商品數量
    @PostMapping("/update")
    public ResponseEntity<?> updateCartItem(@RequestParam("cartItemId") Integer cartItemId,
                                            @RequestParam("quantity") Integer quantity,
                                            @SessionAttribute(value = "user", required = false) Users user) {
        if (user == null) {
            return ResponseEntity.badRequest().body("未登入");
        }
        shoppingCartService.updateCartItem(cartItemId, quantity);
        return ResponseEntity.ok("更新成功");
    }

    // 清空購物車
    @PostMapping("/clear")
    public ResponseEntity<?> clearCart(@SessionAttribute(value = "user", required = false) Users user) {
        if (user == null) {
            return ResponseEntity.badRequest().body("未登入");
        }
        shoppingCartService.clearCart(user.getUserId());
        return ResponseEntity.ok("清空購物車成功");
    }
}
