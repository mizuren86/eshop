package com.eeit1475th.eshop.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eeit1475th.eshop.cart.dto.SelectedStore;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/ecpay")
public class EcpayCallbackController {

    // 支援 POST 請求
    @PostMapping("/callback")
    public String callbackPost(@RequestParam Map<String, String> params, HttpSession session) {
        return handleCallback(params, session);
    }
    
    // 同時支援 GET 請求
    @GetMapping("/callback")
    public String callbackGet(@RequestParam Map<String, String> params, HttpSession session) {
        return handleCallback(params, session);
    }
    
    private String handleCallback(Map<String, String> params, HttpSession session) {
        String storeID = params.get("StoreID");
        String storeName = params.get("StoreName");
        String storeAddress = params.get("StoreAddress");
        String storePhone = params.get("StorePhone");
        // 將門市資料存入 Session
        session.setAttribute("selectedStore", new SelectedStore(storeID, storeName, storeAddress, storePhone));
        // 重新導向回 checkout 頁面
        return "redirect:/checkout";
    }
}
