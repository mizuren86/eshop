package com.eeit1475th.eshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.eeit1475th.eshop.member.entity.Users;
import com.eeit1475th.eshop.member.service.UsersService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UsersService usersService;

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
    public String profilePage(Model model, HttpServletRequest request, HttpSession session) {
        model.addAttribute("pageTitle", "個人資料");

        // 從 session 中獲取用戶資料
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            // 如果 session 中沒有用戶資料，檢查 token
            String token = (String) session.getAttribute("token");
            if (token == null) {
                return "redirect:/login";
            }
            try {
                Users user = usersService.getUserByToken(token);
                if (user != null) {
                    session.setAttribute("user", user);
                    model.addAttribute("user", user);
                } else {
                    return "redirect:/login";
                }
            } catch (Exception e) {
                return "redirect:/login";
            }
        } else {
            // 即使session中有用户数据，也重新从数据库获取最新数据
            try {
                String token = (String) session.getAttribute("token");
                if (token != null) {
                    Users user = usersService.getUserByToken(token);
                    if (user != null) {
                        session.setAttribute("user", user);
                        model.addAttribute("user", user);
                    } else {
                        return "redirect:/login";
                    }
                }
            } catch (Exception e) {
                return "redirect:/login";
            }
        }

        return "/pages/profile";
    }

    // 顯示編輯個人資料頁面
    @GetMapping("/edit-profile")
    public String editProfilePage(Model model, HttpSession session) {
        model.addAttribute("pageTitle", "編輯個人資料");

        // 從 session 中獲取用戶資料
        Object userObj = session.getAttribute("user");
        if (userObj == null) {
            // 如果 session 中沒有用戶資料，檢查 token
            String token = (String) session.getAttribute("token");
            if (token == null) {
                return "redirect:/login";
            }
            try {
                Users user = usersService.getUserByToken(token);
                if (user != null) {
                    session.setAttribute("user", user);
                    model.addAttribute("user", user);
                } else {
                    return "redirect:/login";
                }
            } catch (Exception e) {
                return "redirect:/login";
            }
        } else {
            model.addAttribute("user", userObj);
        }

        return "/pages/edit-profile";
    }

    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("pageTitle", "修改密碼");
        return "/pages/change-password";
    }

    @GetMapping("/logout")
    public String logoutPage(Model model, HttpServletRequest request, HttpServletResponse response) {
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
