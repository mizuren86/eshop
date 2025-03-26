package com.eeit1475th.eshop.review;


import org.thymeleaf.TemplateEngine;

public class ThymeleafVersion {
    public static void main(String[] args) {
        System.out.println("Thymeleaf version: " + TemplateEngine.class.getPackage().getImplementationVersion());
    }
}