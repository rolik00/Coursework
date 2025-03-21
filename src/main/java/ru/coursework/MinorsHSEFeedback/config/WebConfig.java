package ru.coursework.MinorsHSEFeedback.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var allHttpMethods = Arrays.stream(HttpMethod.values()).map(HttpMethod::name).toList();
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000","http://localhost:3001","http://localhost:3002")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "TRACE", "DELETE", "OPTIONS");
    }
}