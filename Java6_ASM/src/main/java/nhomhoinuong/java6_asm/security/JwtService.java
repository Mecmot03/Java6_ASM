package nhomhoinuong.java6_asm.security;

import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import nhomhoinuong.java6_asm.bean.User;

@Service
public class JwtService {

    private static final String SECRET =
            "Java6ASMJWTSecretKeyJava6ASMJWTSecretKey";

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes());

    private static final long EXPIRATION =
            1000 * 60 * 60 * 24;

    // 🟢 1. PHƯƠNG THỨC MỚI CÓ 2 THAM SỐ (User, List<String> roles)
    public String generateToken(User user, List<String> roles) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 🟢 2. GIỮ LẠI PHƯƠNG THỨC 1 THAM SỐ ĐỂ TRÁNH LỖI CÁC FILE KHÁC NẾU CÓ TRUY XUẤT
    public String generateToken(User user) {
        return generateToken(user, List.of("ROLE_USER"));
    }

    // Lấy email từ token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // Kiểm tra token
    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Đọc Claims
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}