package com.example.healthmonitoring.config;

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

import com.example.healthmonitoring.security.JwtAuthFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST API
            .csrf(csrf -> csrf.disable())
            
            // Enable CORS with custom configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Stateless session
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - NO authentication required
                .requestMatchers(
                    "/",                        // Root path
                    "/health",                  // Health check
                    "/version",                 // Version info
                    "/api/v1/auth/**",
                    "/api/v1/ping",
                    "/api/v1/ping/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/error"
                ).permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            
            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ===== CẤU HÌNH MỞ RỘNG CHO DEVELOPMENT =====
        
        // 1. Cho phép TẤT CẢ origins (development only)
        configuration.addAllowedOriginPattern("*");
        
        // 2. Hoặc chỉ định cụ thể các origins (production)
        // configuration.setAllowedOrigins(Arrays.asList(
        //     "http://localhost:3000",           // React
        //     "http://localhost:4200",           // Angular
        //     "http://localhost:8081",           // Flutter web
        //     "http://127.0.0.1:8080",          
        //     "http://10.0.2.2:8080",            // Android emulator
        //     "http://192.168.1.100:8080"        // Local network IP
        // ));
        
        // 3. Cho phép TẤT CẢ HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
        ));
        
        // 4. Cho phép TẤT CẢ headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 5. Expose headers để client có thể đọc
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count",
            "X-Page-Number",
            "X-Page-Size"
        ));
        
        // 6. Cho phép credentials (cookies, authorization headers)
        // CHÚ Ý: Nếu set allowCredentials = true, KHÔNG được dùng "*" cho origins
        configuration.setAllowCredentials(false);
        
        // 7. Cache preflight response trong 1 giờ
        configuration.setMaxAge(3600L);

        // Áp dụng cấu hình cho TẤT CẢ endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}