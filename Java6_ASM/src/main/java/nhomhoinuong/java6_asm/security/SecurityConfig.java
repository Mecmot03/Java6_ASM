package nhomhoinuong.java6_asm.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 1. Tắt CSRF
            .csrf(csrf -> csrf.disable())

            // 2. Tích hợp CorsConfigurationSource chuẩn bên dưới
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // 3. Stateless Session
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 4. Phân quyền
            .authorizeHttpRequests(auth -> auth
                // Cực kỳ quan trọng: Cho phép tất cả request Pre-flight OPTIONS đi qua
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Mở quyền truy cập các API công khai
                .requestMatchers(
                    "/api/auth/**",
                    "/api/products/**",
                    "/api/categories/**",
                    "/api/cart/count"
                ).permitAll()

                // Phân quyền Admin
                .requestMatchers("/api/admin/**", "/api/users/**").hasRole("ADMIN")

                // Tất cả request khác bắt buộc login
                .anyRequest().authenticated()
            )

            // 5. Thêm JWT Filter
            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    // 🔥 CẤU HÌNH CORS DUY NHẤT CHUẨN XÁC DÀNH CHO SPRING SECURITY
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Dùng allowedOriginPatterns thay cho allowedOrigins để tránh lỗi 400 với allowCredentials(true)
        configuration.setAllowedOriginPatterns(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
        
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}