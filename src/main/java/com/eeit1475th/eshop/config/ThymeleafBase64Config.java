package com.eeit1475th.eshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eeit1475th.eshop.util.Base64Util;

@Configuration
public class ThymeleafBase64Config {

    @Bean
    public Base64Util base64Util() {
        return new Base64Util();
    }
}