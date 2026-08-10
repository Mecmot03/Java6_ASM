package nhomhoinuong.java6_asm.dto;

import lombok.Data;

@Data
public class SocialLoginRequest {
    private String email;
    private String fullName;
    private String avatar;
    private String provider; // "GOOGLE" hoặc "FACEBOOK"
}