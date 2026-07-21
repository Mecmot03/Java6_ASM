package nhomhoinuong.java6_asm.security;

import java.security.Key;
import java.util.Date;

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

    private final Key key =
            Keys.hmacShaKeyFor(SECRET.getBytes());

    private static final long EXPIRATION =
            1000 * 60 * 60 * 24;

    // Sinh JWT từ User
    public String generateToken(User user) {

        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith((SecretKey) key, SignatureAlgorithm.HS256)
                .compact();

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
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

}