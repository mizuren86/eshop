package com.eeit1475th.eshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.eeit1475th.eshop.member.entity.Users;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {

    // 返回購物車頁面 (模板位於 /templates/pages/cart.html)
    @GetMapping
    public String showCart(@SessionAttribute(value = "user", required = false) Users user) {
        if (user == null) {
            return "redirect:/login";
        }
        return "/pages/cart";
    }
}
