package com.eeit1475th.eshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.eeit1475th.eshop.member.entity.Users;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class ShoppingCartController {

    // 返回購物車頁面 (模板位於 /templates/pages/cart.html)
    @GetMapping
//    public String showCart(@SessionAttribute(value = "user", required = false) Users user) {
    public String showCart(@SessionAttribute(value = "user", required = false) Users user,HttpSession session) {
//        if (user == null) {
//            return "redirect:/login";
//        }
    	
    	if (user == null) {
            // 暫時指定一個測試用會員（僅供開發測試使用）
            user = new Users();
            user.setUserId(3);
            // 將測試會員存入 Session，後續 API 調用就能使用此會員
            session.setAttribute("user", user);
        }
    	
        return "/pages/cart";
    }
}
