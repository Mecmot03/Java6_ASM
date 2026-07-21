package nhomhoinuong.java6_asm.service;

import nhomhoinuong.java6_asm.bean.User;
import nhomhoinuong.java6_asm.dto.LoginRequest;
import nhomhoinuong.java6_asm.dto.LoginResponse;
import nhomhoinuong.java6_asm.dto.RegisterRequest;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

    User register(RegisterRequest request);

}