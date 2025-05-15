package com.mobbScan_integration.MobbScan.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica a todos los endpoints
                .allowedOriginPatterns("http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:5173") // O lo que necesites// Dominio del frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
