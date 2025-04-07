package com.eeit1475th.eshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    // 顯示登入頁面
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "Login");
        return "/pages/login"; // 對應 templates/pages/login.html
    }

    // 顯示註冊頁面
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("pageTitle", "Register");
        return "/pages/register"; // 對應 templates/pages/register.html
    }

    // 顯示用戶個人資料頁面
    @GetMapping("/profile")
    public String profilePage(Model model) {
        model.addAttribute("pageTitle", "個人資料");
        // 依需求從 session 或 service 中取得用戶資料，加入 model
        return "/pages/profile"; // 對應 templates/pages/profile.html
    }

    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("pageTitle", "修改密碼");
        return "/pages/change-password";
    }

    @GetMapping("/logout")
    public String logoutPage(Model model,HttpServletRequest request,HttpServletResponse response) {
        model.addAttribute("pageTitle", "登出");
     // 清除 Session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 使當前 Session 失效
        }
        
        // 清除瀏覽器的 JSESSIONID Cookie（可選但建議）
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath(request.getContextPath());
        cookie.setMaxAge(0); // 立即過期
        response.addCookie(cookie);
        
        return "redirect:/"; // 導回首頁
    }
}
