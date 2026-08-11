//package nhomhoinuong.java6_asm.dto;
//
//import java.util.List;
//import lombok.Data;
//
//@Data
//public class UserRequest {
//
//    private String fullName;
//
//    private String email;
//
//    private String password;
//
//    private String phone;
//
//    private String address;
//
//    private Boolean enabled;
//
//    private String role;
//
//    private List<String> roles;
//
//}




package nhomhoinuong.java6_asm.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    private String password;

    @Pattern(
        regexp = "^$|^0(3|5|7|8|9)[0-9]{8}$",
        message = "Số điện thoại không đúng định dạng"
    )
    private String phone;

    private String address;

    private Boolean enabled;

    private String role;

    private List<String> roles;
}