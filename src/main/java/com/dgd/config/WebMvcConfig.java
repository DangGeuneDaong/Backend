package com.dgd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080", "http://localhost:8081")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "MESSAGE")
                .allowedHeaders("*")
                .allowedOriginPatterns("*")
                .exposedHeaders("*")
                .allowCredentials(true);
        WebMvcConfigurer.super.addCorsMappings(registry);
    }
}
