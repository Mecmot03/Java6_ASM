package nhomhoinuong.java6_asm.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // Tắt CSRF
                .csrf(csrf -> csrf.disable())

                // Bật CORS
                .cors(Customizer.withDefaults())

                // Không dùng Session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Phân quyền
                .authorizeHttpRequests(auth -> auth

                        // API Public
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/products/**",
                                "/api/categories/**"
                        ).permitAll()

                        // Chỉ Admin
                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        // Quản lý User (Admin)
                        .requestMatchers(
                                "/api/users/**"
                        ).hasRole("ADMIN")

                        // Các API còn lại phải đăng nhập
                        .anyRequest().authenticated()

                )

                // JWT Filter
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();

    }

}