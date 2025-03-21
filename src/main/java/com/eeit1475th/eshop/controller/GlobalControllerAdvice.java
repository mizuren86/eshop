package com.eeit1475th.eshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import com.eeit1475th.eshop.cart.dto.CartItemsDTO;
import com.eeit1475th.eshop.cart.service.ShoppingCartService;
import com.eeit1475th.eshop.member.entity.Users;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @ModelAttribute
    public void addCartCount(Model model, HttpSession session) {
        Users user = (Users) session.getAttribute("user");
        int cartCount = 0;
        if (user != null) {
            List<CartItemsDTO> cartItems = shoppingCartService.getCartItems(user.getUserId());
            cartCount = cartItems.size();
        }
        model.addAttribute("cartCount", cartCount);
    }
}
