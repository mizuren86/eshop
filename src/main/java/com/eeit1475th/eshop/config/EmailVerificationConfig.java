package com.eeit1475th.eshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class EmailVerificationConfig {

    @Bean
    public Set<String> verifiedEmails() {
        return ConcurrentHashMap.newKeySet();
    }

    @Bean
    public Map<String, String> verificationTokens() {
        return new ConcurrentHashMap<>();
    }
}