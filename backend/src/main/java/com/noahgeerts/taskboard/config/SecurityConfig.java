package com.noahgeerts.taskboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.noahgeerts.taskboard.filter.JwtAuthenticationFilter;
import com.noahgeerts.taskboard.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider, CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll() // login, refresh open
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // permit all preflight requests
                        .anyRequest().authenticated() // everything else requires auth
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Add CORS headers to 401 responses
                            response.addHeader("Access-Control-Allow-Origin",
                                    "https://taskboard-omega-mocha.vercel.app/");
                            response.addHeader("Access-Control-Allow-Credentials", "true");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // Add CORS headers to 403 responses
                            response.addHeader("Access-Control-Allow-Origin",
                                    "https://taskboard-omega-mocha.vercel.app/");
                            response.addHeader("Access-Control-Allow-Credentials", "true");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        }))
                .build();
    }
}
