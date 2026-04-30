package com.plataformas_danos_back.config;

import com.plataformas_danos_back.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Evita que Spring Boot registre JwtAuthenticationFilter en el servlet filter chain
 * automáticamente (ya se añade manualmente en SecurityConfig vía addFilterBefore).
 */
@Configuration
@RequiredArgsConstructor
public class JwtFilterConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration() {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(jwtAuthenticationFilter);
        registration.setEnabled(false);
        return registration;
    }
}
