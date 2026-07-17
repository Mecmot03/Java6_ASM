package nhomhoinuong.java6_asm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Tắt CSRF để các request từ Axios của Vue không bị chặn đứng
            .csrf(csrf -> csrf.disable()) 
            
            // 2. Kích hoạt CORS để nhận cấu hình cổng 5173 từ Frontend
            .cors(cors -> {})            
            
            // 3. Mở khóa toàn bộ hệ thống endpoint bắt đầu bằng /api/
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll() // Cho phép truy cập tự do hoàn toàn vào tất cả API
                .anyRequest().permitAll()              // TẠM THỜI: Mở hết mọi đường dẫn khác để test luồng dữ liệu cho mượt
            );

        return http.build();
    }
}