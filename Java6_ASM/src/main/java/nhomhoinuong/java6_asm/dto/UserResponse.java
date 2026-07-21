package nhomhoinuong.java6_asm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String fullName;

    private String email;

    private String phone;

    private String address;

    private String avatar;

    private Boolean enabled;

    private String role;

}