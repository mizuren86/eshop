package com.eeit1475th.eshop.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.eeit1475th.eshop.cart.dto.CartItemsDTO;
import com.eeit1475th.eshop.cart.service.ShoppingCartService;
import com.eeit1475th.eshop.member.entity.Users;

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model, HttpSession session,
            @SessionAttribute(value = "user", required = false) Users user,
            @RequestParam(value="shippingMethod", required=false) String shippingMethod,
            @RequestParam(value="paymentMethod", required=false) String paymentMethod) {
    	
        // 假設使用者ID存在 session 中
        Integer userId = (Integer) session.getAttribute("userId");
        if (user == null) {
            return "redirect:/login?redirect=/checkout";
        }
    	
        // 取得該使用者購物車商品列表
        List<CartItemsDTO> cartItems = shoppingCartService.getCartItems(userId);
        model.addAttribute("cartItems", cartItems);
        
        // 從 Session 讀取優惠券折扣，如果沒有就預設為 0
        Integer couponDiscount = (Integer) session.getAttribute("couponDiscount");
        if (couponDiscount == null) {
            couponDiscount = 0;
            session.setAttribute("couponDiscount", couponDiscount);
        }
        model.addAttribute("couponDiscount", couponDiscount);

        // 計算購物車小計
        BigDecimal totalAmount = cartItems.stream()
                .map(CartItemsDTO::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalAmount", totalAmount);
        
        // 儲存運送與付款方式到 Session
        if (shippingMethod != null && !shippingMethod.trim().isEmpty()) {
            session.setAttribute("shippingMethod", shippingMethod.trim());
        } else if (session.getAttribute("shippingMethod") == null) {
            session.setAttribute("shippingMethod", "711-cod");
        }
        if (paymentMethod != null && !paymentMethod.trim().isEmpty()) {
            session.setAttribute("paymentMethod", paymentMethod.trim());
        } else if (session.getAttribute("paymentMethod") == null) {
            session.setAttribute("paymentMethod", "711-cod-only");
        }
        String sessionShippingMethod = (String) session.getAttribute("shippingMethod");
        String sessionPaymentMethod = (String) session.getAttribute("paymentMethod");
        model.addAttribute("shippingMethod", sessionShippingMethod);
        model.addAttribute("paymentMethod", sessionPaymentMethod);
        
        // 取得會員資料
        model.addAttribute("userId", userId);
        model.addAttribute("users", user);
        
        return "/pages/checkout";  // 對應到 src/main/resources/templates/checkout.html
    }
}
