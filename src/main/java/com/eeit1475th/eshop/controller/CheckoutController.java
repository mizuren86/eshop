package com.eeit1475th.eshop.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
//    public String showCheckoutPage(Model model, HttpSession session) {
    public String showCheckoutPage(Model model, HttpSession session,@SessionAttribute(value = "user", required = false) Users user) {
//        // 假設使用者ID存在 session 中
//        Integer userId = (Integer) session.getAttribute("userId");
//        if (userId == null) {
//            // 若無使用者資訊，轉導到登入頁面
//            return "redirect:/login";
//        }
    	
    	if (user == null) {
            user = new Users();
            user.setUserId(3);  // 測試用會員 ID，可依需求調整
            session.setAttribute("user", user);
        }
        Integer userId = user.getUserId();
        
        // 取得該使用者購物車商品列表
        List<CartItemsDTO> cartItems = shoppingCartService.getCartItems(userId);
        model.addAttribute("cartItems", cartItems);
        
        // 計算總金額（假設 CartItemsDTO 有 subTotal 屬性）
        BigDecimal totalAmount = cartItems.stream()
            .map(item -> item.getSubTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("userId", userId);
        
        return "/pages/checkout";  // 對應到 src/main/resources/templates/checkout.html
    }
}
