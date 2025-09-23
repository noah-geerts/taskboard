package com.noahgeerts.taskboard.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.noahgeerts.taskboard.service.CustomUserDetailsService;

@Configuration
public class TaskBoardConfig {
    private CustomUserDetailsService userDetailsService;

    public TaskBoardConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000",
                                "https://taskboard-q0owc1i7c-noahs-projects-8c520327.vercel.app/")
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new DaoAuthenticationProvider(userDetailsService);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }
}
