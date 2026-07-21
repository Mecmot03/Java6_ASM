package nhomhoinuong.java6_asm.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nhomhoinuong.java6_asm.bean.Authority;
import nhomhoinuong.java6_asm.bean.User;
import nhomhoinuong.java6_asm.dao.AuthorityDAO;
import nhomhoinuong.java6_asm.dao.UserDAO;
import nhomhoinuong.java6_asm.dto.LoginRequest;
import nhomhoinuong.java6_asm.dto.LoginResponse;
import nhomhoinuong.java6_asm.dto.RegisterRequest;
import nhomhoinuong.java6_asm.security.JwtService;
import nhomhoinuong.java6_asm.service.AuthenticationService;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserDAO userDAO;
    private final AuthorityDAO authorityDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {

        // Kiểm tra email
        User user = userDAO.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Sai mật khẩu");
        }

        // Kiểm tra tài khoản
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        // Sinh JWT
        String token = jwtService.generateToken(user);

        // Lấy role
        String role = user.getAuthorities()
                .stream()
                .findFirst()
                .map(Authority::getRole)
                .orElse("ROLE_USER");

        // Trả về cho Vue
        return new LoginResponse(
                token,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                role
        );
    }

    @Override
    public User register(RegisterRequest request) {

        // Kiểm tra email đã tồn tại
        if (userDAO.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Tạo User mới
        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        // Lưu User
        User savedUser = userDAO.save(user);

        // Gán quyền mặc định
        Authority authority = new Authority();
        authority.setUser(savedUser);
        authority.setRole("ROLE_USER");

        authorityDAO.save(authority);

        return savedUser;
    }
}