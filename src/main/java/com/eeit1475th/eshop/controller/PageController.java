package com.eeit1475th.eshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "/pages/forgot-password";
    }
}