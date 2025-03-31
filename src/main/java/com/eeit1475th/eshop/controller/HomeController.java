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
        return "/index";
    }
    
}
