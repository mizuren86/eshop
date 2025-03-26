package com.eeit1475th.eshop.member.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.eeit1475th.eshop.member.filter.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .maximumSessions(1)
                        .expiredUrl("/login?expired")
                        .maxSessionsPreventsLogin(true))
                .authorizeHttpRequests(auth -> auth
                        // 允许所有页面和静态资源访问
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/index").permitAll()
                        .requestMatchers("/index/**").permitAll()
                        .requestMatchers("/*.html").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/lib/**", "/favicon.ico")
                        .permitAll()
                        .requestMatchers("/pages/**").permitAll()
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/*.jpg", "/*.png", "/*.gif", "/*.ico").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/register/**").permitAll()
                        .requestMatchers("/shop").permitAll()
                        .requestMatchers("/shop/**").permitAll()
                        .requestMatchers("/shop-detail").permitAll()
                        .requestMatchers("/shop-detail/**").permitAll()
                        .requestMatchers("/shop-cart").permitAll()
                        .requestMatchers("/shop-cart/**").permitAll()
                        .requestMatchers("/shop-checkout").permitAll()
                        .requestMatchers("/shop-checkout/**").permitAll()
                        .requestMatchers("/shop-order").permitAll()
                        .requestMatchers("/shop-order/**").permitAll()
                        .requestMatchers("/shop-profile").permitAll()
                        .requestMatchers("/shop-profile/**").permitAll()
                        .requestMatchers("/contact").permitAll()
                        .requestMatchers("/contact/**").permitAll()
                        .requestMatchers("/about").permitAll()
                        .requestMatchers("/about/**").permitAll()
                        .requestMatchers("/faq").permitAll()
                        .requestMatchers("/faq/**").permitAll()
                        .requestMatchers("/privacy").permitAll()
                        .requestMatchers("/privacy/**").permitAll()
                        .requestMatchers("/terms").permitAll()
                        .requestMatchers("/terms/**").permitAll()

                        // 允许所有公开 API 访问
                        .requestMatchers("/api/users/login", "/api/users/register", "/api/users/send-verification")
                        .permitAll()
                        .requestMatchers("/api/users/verify").permitAll()
                        .requestMatchers("/api/users/verify-email").permitAll()
                        .requestMatchers("/api/users/forgot-password", "/api/users/reset-password").permitAll()
                        .requestMatchers("/api/users/current").permitAll()
                        .requestMatchers("/api/users/logout").permitAll()
                        .requestMatchers("/api/products/**").permitAll()
                        .requestMatchers("/api/categories/**").permitAll()
                        .requestMatchers("/api/chat/**").permitAll()
                        .requestMatchers("/error", "/error.html").permitAll()

                        // 需要认证的 API
                        .requestMatchers("/api/users/profile").authenticated()
                        .requestMatchers("/api/users/update").authenticated()
                        .requestMatchers("/api/users/password").authenticated()
                        .requestMatchers("/api/orders/**").authenticated()
                        .requestMatchers("/api/cart/**").authenticated()
                        .requestMatchers("/api/reviews/**").authenticated()

                        // 其他所有请求都需要认证
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"error\":\"未授权的访问\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"error\":\"访问被拒绝\"}");
                        }))
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8091", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}