package com.eeit1475th.eshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeit1475th.eshop.cart.dto.OrderDTO;
import com.eeit1475th.eshop.cart.service.OrdersService;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {
    
    @Autowired
    private OrdersService ordersService;
    
    @GetMapping("/orders")
    public String showOrders(@RequestParam(value = "page", defaultValue = "0") int page, 
                             HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login?redirect=/orders";
        }
        // 每頁顯示 6 筆訂單，並依訂單成立時間由新到舊排序
        Page<OrderDTO> ordersPage = ordersService.getOrdersByUser(userId, page, 6);
        model.addAttribute("ordersPage", ordersPage);
        
        // 清除在訂單流程中存入的 session 屬性
        session.removeAttribute("shippingMethod");
        session.removeAttribute("paymentMethod");
        session.removeAttribute("couponDiscount");
		session.removeAttribute("selectedCity");
		session.removeAttribute("selectedDistrict");
        
        return "/pages/orders";
    }

}