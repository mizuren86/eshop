package com.eeit1475th.eshop.member.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}