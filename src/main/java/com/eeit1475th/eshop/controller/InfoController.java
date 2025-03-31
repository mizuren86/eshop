package com.eeit1475th.eshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoController {
	
	@GetMapping("/aboutus")
    public String aboutUs(Model model) {
        model.addAttribute("pageTitle", "About Us");
        return "/pages/aboutus";
    }
	
	@GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Contact");
        return "/pages/contact";
    }
	
	@GetMapping("/privacypolicy")
    public String privacyPolicy(Model model) {
        model.addAttribute("pageTitle", "Privacy Policy");
        return "/pages/privacypolicy";
    }
	
	@GetMapping("/termsofuse")
    public String termsofUse(Model model) {
        model.addAttribute("pageTitle", "Terms of Use");
        return "/pages/termsofuse";
    }
	
	@GetMapping("/salesandrefunds")
    public String salesandRefunds(Model model) {
        model.addAttribute("pageTitle", "Sales and Refunds");
        return "/pages/salesandrefunds";
    }
	
	@GetMapping("/faqshelp")
    public String faqsHelp(Model model) {
        model.addAttribute("pageTitle", "FAQs & Help");
        return "/pages/faqshelp";
    }
	

	
	
	
	
	
    @GetMapping("/testimonial")
    public String testimonial(Model model) {
        model.addAttribute("pageTitle", "Testimonial");
        return "/pages/testimonial";
    }
    
    @GetMapping("/404")
    public String error404(Model model) {
        model.addAttribute("pageTitle", "404 Error");
        return "/404";
    }
}
