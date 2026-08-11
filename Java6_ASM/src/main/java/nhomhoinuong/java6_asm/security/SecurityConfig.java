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

                // 1. PUBLIC API (Không cần đăng nhập)
                .requestMatchers(
                    "/api/auth/**",
                    "/api/products/**",
                    "/api/categories/**",
                    "/api/cart/**",  
                    "/api/comments/**",
                    "/api/favorites/**"
                ).permitAll()

                // 2. CHỈ ROLE_USER: Mới được tạo đơn & Mua lại đơn
                .requestMatchers("/api/orders/create").hasRole("USER")
                .requestMatchers("/api/orders/{orderId}/rebuy").hasRole("USER")

                // 3. XEM DANH SÁCH ĐƠN: Cho phép cả USER, STAFF và ADMIN để xem/xử lý đơn
                .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAnyRole("USER", "STAFF", "ADMIN")

                // 4. HỦY ĐƠN HÀNG: Cả USER, STAFF, ADMIN đều có thể gửi yêu cầu hủy đơn
                .requestMatchers(HttpMethod.PUT, "/api/orders/{orderId}/cancel").hasAnyRole("USER", "STAFF", "ADMIN")

                // 5. DUYỆT & CẬP NHẬT ĐƠN HÀNG: Cho phép cả STAFF và ADMIN duyệt đơn
                .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasAnyRole("STAFF", "ADMIN")

                // 6. CHỈ ROLE_ADMIN: Mới được vào trang Quản trị Hệ thống
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