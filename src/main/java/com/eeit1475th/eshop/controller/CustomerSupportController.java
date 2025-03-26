package com.eeit1475th.eshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eeit1475th.eshop.chat.dto.CustomerSupportWebSocketHandler;


@RestController
public class CustomerSupportController {

    @Autowired
    private CustomerSupportWebSocketHandler customerSupportWebSocketHandler;

    @PostMapping("/chat/toggle-mode")
    public String toggleMode() {
        customerSupportWebSocketHandler.toggleHumanMode();
        return "{ \"status\": \"success\", \"isHumanMode\": " + customerSupportWebSocketHandler.isHumanMode() + " }";
    }
}
