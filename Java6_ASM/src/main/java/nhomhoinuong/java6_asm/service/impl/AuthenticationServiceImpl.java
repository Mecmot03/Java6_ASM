package nhomhoinuong.java6_asm.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
    private final RestTemplate restTemplate = new RestTemplate();

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

    // TOÀN BỘ LOGIC XÁC THỰC SOCIAL NẰM TRỌN VẸN Ở BACKEND
    @Override
    @Transactional
    public LoginResponse socialLogin(SocialLoginRequest request) {
        String email = "";
        String fullName = "";
        String avatar = "";

        // 1. Backend gọi sang Google/Facebook verify token
        if ("GOOGLE".equalsIgnoreCase(request.getProvider())) {
            String url = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + request.getToken();
            try {
                Map<String, Object> googleUser = restTemplate.getForObject(url, Map.class);
                if (googleUser == null || !googleUser.containsKey("email")) {
                    throw new RuntimeException("Token Google không hợp lệ!");
                }
                email = (String) googleUser.get("email");
                fullName = (String) googleUser.get("name");
                avatar = (String) googleUser.get("picture");
            } catch (Exception e) {
                throw new RuntimeException("Xác thực Google thất bại: " + e.getMessage());
            }
        } else if ("FACEBOOK".equalsIgnoreCase(request.getProvider())) {
            String url = "https://graph.facebook.com/v18.0/me?fields=id,name,email,picture&access_token=" + request.getToken();
            try {
                Map<String, Object> fbUser = restTemplate.getForObject(url, Map.class);
                if (fbUser == null) {
                    throw new RuntimeException("Token Facebook không hợp lệ!");
                }
                String fbId = (String) fbUser.get("id");
                email = fbUser.containsKey("email") ? (String) fbUser.get("email") : fbId + "@facebook.com";
                fullName = (String) fbUser.get("name");

                if (fbUser.containsKey("picture")) {
                    Map<String, Object> picture = (Map<String, Object>) fbUser.get("picture");
                    Map<String, Object> data = (Map<String, Object>) picture.get("data");
                    avatar = (String) data.get("url");
                }
            } catch (Exception e) {
                throw new RuntimeException("Xác thực Facebook thất bại: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Provider không hợp lệ!");
        }

        // 2. Tự tạo User mới nếu lần đầu đăng nhập, hoặc lấy User cũ
        final String finalEmail = email;
        final String finalFullName = fullName;
        final String finalAvatar = avatar;

        User user = userDAO.findByEmail(finalEmail).orElseGet(() -> {
            User newUser = new User();
            newUser.setFullName(finalFullName);
            newUser.setEmail(finalEmail);
            newUser.setAvatar(finalAvatar);
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

        if (finalAvatar != null && !finalAvatar.equals(user.getAvatar())) {
            user.setAvatar(finalAvatar);
            userDAO.save(user);
        }

        // 3. Trả về LoginResponse chứa Jwt Token + Roles
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