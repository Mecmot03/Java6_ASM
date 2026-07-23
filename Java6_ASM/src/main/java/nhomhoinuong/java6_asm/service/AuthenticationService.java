package nhomhoinuong.java6_asm.service;

import nhomhoinuong.java6_asm.bean.User;
import nhomhoinuong.java6_asm.dto.LoginRequest;
import nhomhoinuong.java6_asm.dto.LoginResponse;
import nhomhoinuong.java6_asm.dto.RegisterRequest;
import nhomhoinuong.java6_asm.dto.SocialLoginRequest;
import nhomhoinuong.java6_asm.dto.UserRequest;
import nhomhoinuong.java6_asm.dto.UserResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

    User register(RegisterRequest request);

    LoginResponse socialLogin(SocialLoginRequest request); 

    UserResponse updateCurrentUser(Long userId, UserRequest request);

}