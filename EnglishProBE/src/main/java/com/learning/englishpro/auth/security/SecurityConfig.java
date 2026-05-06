package com.learning.englishpro.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.Customizer;

import java.util.Arrays;

/**
 * Spring Security 6 configuration.
 * Stateless JWT — no sessions, no CSRF.
 * Method-level security enabled (@PreAuthorize).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;

        // ── Public endpoints ─────────────────────────────────────────────────────
        private static final String[] PUBLIC_POST = {
                        "/auth/register",
                        "/auth/login",
                        "/auth/refresh-token",
                        "/payments/webhook",
                        "/ai/essays/grade",
                        "/dev/seed"
        };

        private static final String[] PUBLIC_GET = {
                        "/courses",
                        "/courses/**",
                        "/files/**",
                        "/payments/return",        // VNPay redirect sau thanh toán
                        "/payments/webhook",       // VNPay IPN via GET
                        "/payments/order-info/**", // Lấy thông tin đơn hàng cho QR
                        "/coupons/validate/**"     // Cho phép học viên / guest validate mã
        };

        private static final String[] SWAGGER_WHITELIST = {
                        "/v3/api-docs/**",
                        "/v1/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
        };

        // ── SecurityFilterChain ──────────────────────────────────────────────────
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .cors(Customizer.withDefaults()) // Kích hoạt CORS với các thiết lập mặc định từ Bean
                                                                 // bên dưới
                                .csrf(AbstractHttpConfigurer::disable)
                                .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Cho phép Iframe hiển thị file
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(SWAGGER_WHITELIST).permitAll()
                                                .requestMatchers(HttpMethod.POST, PUBLIC_POST).permitAll()
                                                .requestMatchers(HttpMethod.GET, PUBLIC_GET).permitAll()
                                                .anyRequest().authenticated())
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                                                        response.setContentType("application/json");
                                                        response.getWriter().write("{\"status\": 401, \"message\": \"Yêu cầu xác thực (Token có thể đã hết hạn)\"}");
                                                })
                                )
                                .build();
        }

        /**
         * Bean cấu hình CORS chi tiết.
         * Cho phép Frontend (Vite/React/Vue) truy cập vào Backend.
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                // Chỉ định chính xác các Origin được phép truy cập
                configuration.setAllowedOrigins(Arrays.asList(
                                "http://localhost:5173", // URL mặc định của Vite (Vue/React)
                                "http://localhost:5174"  // Dự phòng nếu port 5173 bị chiếm dụng
                ));
                // Các phương thức được phép
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                // Các Header được phép gửi lên
                configuration.setAllowedHeaders(
                                Arrays.asList("Authorization", "Content-Type", "Cache-Control", "X-Requested-With"));
                // Cho phép gửi kèm Credentials (cookies, auth headers)
                configuration.setAllowCredentials(true);
                // Thời gian cache cấu hình CORS này tại trình duyệt (3600s = 1h)
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration); // Áp dụng cho toàn bộ API
                return source;
        }
}
