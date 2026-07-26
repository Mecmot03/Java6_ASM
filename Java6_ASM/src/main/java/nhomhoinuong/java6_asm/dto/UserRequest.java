package nhomhoinuong.java6_asm.dto;

import java.util.List;
import lombok.Data;

@Data
public class UserRequest {

    private String fullName;

    private String email;

    private String password;

    private String phone;

    private String address;

    private Boolean enabled;

    private String role;

    private List<String> roles;

}