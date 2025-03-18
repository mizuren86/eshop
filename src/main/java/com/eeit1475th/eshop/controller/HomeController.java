package com.eeit1475th.eshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 首頁
    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("pageTitle", "Home");
        // 對應 templates/pages/index.html
        return "/index";
    }

    

    
    
    

    // Testimonial 頁面
    @GetMapping("/testimonial")
    public String testimonial(Model model) {
        model.addAttribute("pageTitle", "Testimonial");
        // 對應 templates/pages/testimonial.html
        return "/pages/testimonial";
    }

    // Contact 頁面
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Contact");
        // 對應 templates/pages/contact.html
        return "/pages/contact";
    }

    // 404 Error 頁面
    @GetMapping("/404")
    public String error404(Model model) {
        model.addAttribute("pageTitle", "404 Error");
        // 對應 templates/pages/404.html
        return "/404";
    }
}
