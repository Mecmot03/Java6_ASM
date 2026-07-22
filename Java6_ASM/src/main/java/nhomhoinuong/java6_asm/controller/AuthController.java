package nhomhoinuong.java6_asm.controller;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import nhomhoinuong.java6_asm.bean.User;
import nhomhoinuong.java6_asm.dto.LoginRequest;
import nhomhoinuong.java6_asm.dto.LoginResponse;
import nhomhoinuong.java6_asm.dto.RegisterRequest;
import nhomhoinuong.java6_asm.dto.SocialLoginRequest;
import nhomhoinuong.java6_asm.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authenticationService.login(request);
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return authenticationService.register(request);
    }

    @PostMapping("/social-login")
    public LoginResponse socialLogin(@RequestBody SocialLoginRequest request) {
        return authenticationService.socialLogin(request);
    }

}