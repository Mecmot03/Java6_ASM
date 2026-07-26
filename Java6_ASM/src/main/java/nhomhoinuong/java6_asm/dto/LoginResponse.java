package nhomhoinuong.java6_asm.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private Long id;

    private String fullName;

    private String email;

    private String role; // Role chính

    private List<String> roles; 
}