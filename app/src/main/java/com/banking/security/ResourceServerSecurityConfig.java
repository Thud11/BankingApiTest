package com.banking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class ResourceServerSecurityConfig {

    private static final String PUBLIC_ENDPOINT = "/mock/**";
    private static final String PRIVATE_ENDPOINT = "/api/**";

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(PRIVATE_ENDPOINT)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain mockSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(PUBLIC_ENDPOINT)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINT).permitAll()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers(PUBLIC_ENDPOINT))
                .oauth2ResourceServer(AbstractHttpConfigurer::disable);
        return http.build();


    }
}