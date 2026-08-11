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
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 1. API Công khai (Xem sản phẩm, danh mục, giỏ hàng, auth...)
                .requestMatchers(
                    "/api/auth/**",
                    "/api/products/**",
                    "/api/categories/**",
                    "/api/cart/**",  
                    "/api/comments/**",
                    "/api/favorites/**"
                ).permitAll()

//<<<<<<< HEAD
                
             // 🟢 API Quản lý Đơn hàng:STAFF được vào xác nhận/duyệt đơn
                .requestMatchers("/api/orders/**").hasAnyRole( "STAFF")
//=======
                // 2. CHỈ ROLE_USER mới được Đặt hàng & Xem lịch sử đơn hàng cá nhân
                .requestMatchers("/api/orders/create").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/orders/**").hasRole("USER")
//>>>>>>> main

                // 3. CHỈ ROLE_STAFF mới được Duyệt / Cập nhật trạng thái đơn hàng (Xác nhận, Giao, Hủy)
                .requestMatchers("/api/orders/**").hasRole("STAFF")

                // 4. CHỈ ROLE_ADMIN mới được vào Các API Quản trị hệ thống (Quản lý User, Sản phẩm...)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Các API còn lại yêu cầu đăng nhập
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:*",
            "http://127.0.0.1:*"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
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