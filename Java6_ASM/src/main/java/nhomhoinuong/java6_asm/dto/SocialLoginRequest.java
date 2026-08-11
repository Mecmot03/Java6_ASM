package nhomhoinuong.java6_asm.dto;

import lombok.Data;

@Data
public class SocialLoginRequest {
    private String token;    // Token gửi từ Google/Facebook SDK ở Frontend
    private String email;    // Dự phòng
    private String fullName; // Dự phòng
    private String avatar;   // Dự phòng
    private String provider; // "GOOGLE" hoặc "FACEBOOK"
}