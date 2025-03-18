package com.eeit1475th.eshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    // 顯示登入頁面
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "Login");
        return "/pages/login";  // 對應 templates/pages/login.html
    }

    // 顯示註冊頁面
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("pageTitle", "Register");
        return "/pages/register";  // 對應 templates/pages/register.html
    }

    // 顯示用戶個人資料頁面
    @GetMapping("/profile")
    public String profilePage(Model model) {
        model.addAttribute("pageTitle", "Profile");
        // 依需求從 session 或 service 中取得用戶資料，加入 model
        return "/pages/profile";  // 對應 templates/pages/profile.html
    }
}
