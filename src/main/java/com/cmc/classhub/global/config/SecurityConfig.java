package com.cmc.classhub.global.config;

import com.cmc.classhub.global.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .headers(headersConfigurer -> headersConfigurer
                                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)) // For
                                                                                                                 // H2
                                                                                                                 // DB
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/health",
                                                                "/h2-console/**",
                                                                "/api/reservations",
                                                                "/api/reservations/**",
                                                                "/api/students/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();

                config.setAllowedOrigins(List.of(
                                "http://localhost:3000",
                                "http://localhost:3001",
                                "https://classhub-dashboard.vercel.app",
                                "https://classhub-link.vercel.app"));

                config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));

                // 쿠키/세션 기반이면 true 유지
                // JWT만 쓸 거면 false여도 됨
                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }

        @Bean
        PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
