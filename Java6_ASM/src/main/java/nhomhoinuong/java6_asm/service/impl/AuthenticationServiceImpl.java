package nhomhoinuong.java6_asm.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

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
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Override
	public LoginResponse login(LoginRequest request) {

		// Kiểm tra email
		User user = userDAO.findByEmail(request.getEmail())
				.orElseThrow(() -> new RuntimeException("Email không tồn tại"));

		// Kiểm tra mật khẩu
		boolean passwordMatched = passwordEncoder.matches(request.getPassword(), user.getPassword());
		if (!passwordMatched && request.getPassword() != null && request.getPassword().equals(user.getPassword())) {
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			userDAO.save(user);
			passwordMatched = true;
		}
		if (!passwordMatched) {
			throw new RuntimeException("Sai mật khẩu");
		}

		// Kiểm tra tài khoản
		if (!Boolean.TRUE.equals(user.getEnabled())) {
			throw new RuntimeException("Tài khoản đã bị khóa");
		}

		// Sinh JWT
		String token = jwtService.generateToken(user);

		// Lấy role
		String role = authorityDAO.findByUser_Id(user.getId())
				.map(Authority::getRole)
				.orElse("ROLE_USER");

		// Trả về cho Vue
		return new LoginResponse(token, user.getId(), user.getFullName(), user.getEmail(), role);
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

	@Override
	public LoginResponse socialLogin(SocialLoginRequest request) {

		// 1. Tìm user theo Email, nếu chưa có thì tự động đăng ký
		User user = userDAO.findByEmail(request.getEmail()).orElseGet(() -> {
			User newUser = new User();
			newUser.setFullName(request.getFullName());
			newUser.setEmail(request.getEmail());
			newUser.setAvatar(request.getAvatar());
			// Tạo mật khẩu ngẫu nhiên an toàn cho tài khoản OAuth
			newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
			newUser.setEnabled(true);
			newUser.setCreatedAt(LocalDateTime.now());

			User savedUser = userDAO.save(newUser);

			// Gán quyền mặc định ROLE_USER
			Authority authority = new Authority();
			authority.setUser(savedUser);
			authority.setRole("ROLE_USER");
			authorityDAO.save(authority);

			return savedUser;
		});

		// 2. Kiểm tra trạng thái tài khoản
		if (!Boolean.TRUE.equals(user.getEnabled())) {
			throw new RuntimeException("Tài khoản đã bị khóa");
		}

		// 3. Cập nhật lại Avatar mới nhất nếu có thay đổi
		if (request.getAvatar() != null && !request.getAvatar().equals(user.getAvatar())) {
			user.setAvatar(request.getAvatar());
			userDAO.save(user);
		}

		// 4. Sinh JWT Token
		String token = jwtService.generateToken(user);

		// 5. Lấy Role
		String role = authorityDAO.findByUser_Id(user.getId())
				.map(Authority::getRole)
				.orElse("ROLE_USER");

		// 6. Trả về LoginResponse
		return new LoginResponse(token, user.getId(), user.getFullName(), user.getEmail(), role);
	}

	@Override
	public UserResponse updateCurrentUser(Long userId, UserRequest request) {
		User user = userDAO.findById(userId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

		if (request.getFullName() != null) {
			user.setFullName(request.getFullName());
		}
		if (request.getPhone() != null) {
			user.setPhone(request.getPhone());
		}
		if (request.getAddress() != null) {
			user.setAddress(request.getAddress());
		}
		if (request.getPassword() != null && !request.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(request.getPassword()));
		}

		user.setUpdatedAt(LocalDateTime.now());
		User savedUser = userDAO.save(user);

		String role = authorityDAO.findByUser_Id(savedUser.getId())
				.map(Authority::getRole)
				.orElse("ROLE_USER");

		return UserResponse.builder()
				.id(savedUser.getId())
				.fullName(savedUser.getFullName())
				.email(savedUser.getEmail())
				.phone(savedUser.getPhone())
				.address(savedUser.getAddress())
				.avatar(savedUser.getAvatar())
				.enabled(savedUser.getEnabled())
				.role(role)
				.build();
	}
}