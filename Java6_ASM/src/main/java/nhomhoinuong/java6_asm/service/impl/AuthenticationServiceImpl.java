package nhomhoinuong.java6_asm.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nhomhoinuong.java6_asm.bean.Authority;
import nhomhoinuong.java6_asm.bean.Role;
import nhomhoinuong.java6_asm.bean.User;
import nhomhoinuong.java6_asm.dao.AuthorityDAO;
import nhomhoinuong.java6_asm.dao.RoleDAO;
import nhomhoinuong.java6_asm.dao.UserDAO;
import nhomhoinuong.java6_asm.dto.LoginRequest;
import nhomhoinuong.java6_asm.dto.LoginResponse;
import nhomhoinuong.java6_asm.dto.RegisterRequest;
import nhomhoinuong.java6_asm.dto.SocialLoginRequest;
import nhomhoinuong.java6_asm.dto.UserRequest;
import nhomhoinuong.java6_asm.dto.UserResponse;
import nhomhoinuong.java6_asm.security.JwtService;
import nhomhoinuong.java6_asm.service.AuthenticationService;

@Service
@RequiredArgsConstructor  
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserDAO userDAO;
    private final AuthorityDAO authorityDAO;
    private final RoleDAO roleDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // 🟢 LẤY TẤT CẢ CÁC ROLE CỦA USER DƯỚI DẠNG MẢNG LIST<STRING>
    private List<String> extractAllRoles(Long userId) {
        List<Authority> authorities = authorityDAO.findByUser_Id(userId);
        if (authorities == null || authorities.isEmpty()) {
            return List.of("ROLE_USER");
        }
        return authorities.stream()
                .filter(a -> a.getRole() != null)
                .map(a -> a.getRole().getName())
                .collect(Collectors.toList());
    }

    private String extractPrimaryRole(Long userId) {
        List<String> roles = extractAllRoles(userId);
        if (roles.contains("ROLE_ADMIN") || roles.contains("ADMIN")) {
            return "ROLE_ADMIN";
        }
        if (roles.contains("ROLE_STAFF") || roles.contains("STAFF")) {
            return "ROLE_STAFF";
        }
        return roles.get(0);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userDAO.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Sai mật khẩu");
        }

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        // 🟢 LẤY ĐỦ DANH SÁCH ROLES VÀ TRUYỀN VÀO TOKEN & RESPONSE
        List<String> roles = extractAllRoles(user.getId());
        String token = jwtService.generateToken(user, roles);
        String primaryRole = extractPrimaryRole(user.getId());

        return new LoginResponse(token, user.getId(), user.getFullName(), user.getEmail(), primaryRole, roles);
    }

    @Override
    public User register(RegisterRequest request) {
        if (userDAO.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userDAO.save(user);

        Role userRole = roleDAO.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Role: ROLE_USER"));

        Authority authority = new Authority();
        authority.setUser(savedUser);
        authority.setRole(userRole);

        authorityDAO.save(authority);

        return savedUser;
    }

    @Override
    public LoginResponse socialLogin(SocialLoginRequest request) {
        User user = userDAO.findByEmail(request.getEmail()).orElseGet(() -> {
            User newUser = new User();
            newUser.setFullName(request.getFullName());
            newUser.setEmail(request.getEmail());
            newUser.setAvatar(request.getAvatar());
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            newUser.setEnabled(true);
            newUser.setCreatedAt(LocalDateTime.now());

            User savedUser = userDAO.save(newUser);

            Role userRole = roleDAO.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Role: ROLE_USER"));

            Authority authority = new Authority();
            authority.setUser(savedUser);
            authority.setRole(userRole);
            authorityDAO.save(authority);

            return savedUser;
        });

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        if (request.getAvatar() != null && !request.getAvatar().equals(user.getAvatar())) {
            user.setAvatar(request.getAvatar());
            userDAO.save(user);
        }

        List<String> roles = extractAllRoles(user.getId());
        String token = jwtService.generateToken(user, roles);
        String primaryRole = extractPrimaryRole(user.getId());

        return new LoginResponse(token, user.getId(), user.getFullName(), user.getEmail(), primaryRole, roles);
    }

    @Override
    public UserResponse updateCurrentUser(Long userId, UserRequest request) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userDAO.save(user);

        List<String> roles = extractAllRoles(savedUser.getId());
        String primaryRole = extractPrimaryRole(savedUser.getId());

        return UserResponse.builder()
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .address(savedUser.getAddress())
                .avatar(savedUser.getAvatar())
                .enabled(savedUser.getEnabled())
                .role(primaryRole)
                .roles(roles)
                .build();
    }
}