package nhomhoinuong.java6_asm.dto;

import lombok.Data;

@Data
public class SocialLoginRequest {
<<<<<<< HEAD
    private String email;
    private String fullName;
    private String avatar;
=======
    private String token;    // Token gửi từ Google/Facebook SDK ở Frontend
    private String email;    // Dự phòng
    private String fullName; // Dự phòng
    private String avatar;   // Dự phòng
>>>>>>> 3a2e9b9ebd372cce2d528afda0239ffe85fc073a
    private String provider; // "GOOGLE" hoặc "FACEBOOK"
}