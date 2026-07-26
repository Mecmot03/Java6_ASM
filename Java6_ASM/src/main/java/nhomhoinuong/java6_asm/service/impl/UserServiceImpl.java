package nhomhoinuong.java6_asm.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nhomhoinuong.java6_asm.bean.Authority;
import nhomhoinuong.java6_asm.bean.Role;
import nhomhoinuong.java6_asm.bean.User;
import nhomhoinuong.java6_asm.dao.AuthorityDAO;
import nhomhoinuong.java6_asm.dao.RoleDAO;
import nhomhoinuong.java6_asm.dao.UserDAO;
import nhomhoinuong.java6_asm.dto.UserRequest;
import nhomhoinuong.java6_asm.dto.UserResponse;
import nhomhoinuong.java6_asm.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserDAO userDAO;
	private final AuthorityDAO authorityDAO;
	private final RoleDAO roleDAO;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public List<UserResponse> getAllUsers() {
		return userDAO.findAll().stream().map(this::convertToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getUserById(Long id) {
		User user = userDAO.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

		return convertToResponse(user);
	}

	@Override
	@Transactional
	public UserResponse createUser(UserRequest request) {
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
		user.setUpdatedAt(LocalDateTime.now());

		User savedUser = userDAO.save(user);

		// 🟢 Cấp các quyền được tick
		saveUserAuthorities(savedUser, request);

		return convertToResponse(savedUser);
	}

	@Override
	@Transactional
	public UserResponse updateUser(Long id, UserRequest request) {
		User user = userDAO.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

		if (!user.getEmail().equals(request.getEmail()) && userDAO.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email đã tồn tại");
		}

		user.setFullName(request.getFullName());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setAddress(request.getAddress());

		if (request.getPassword() != null && !request.getPassword().isBlank()) {
			user.setPassword(passwordEncoder.encode(request.getPassword()));
		}

		user.setUpdatedAt(LocalDateTime.now());
		userDAO.save(user);

		// 🟢 Xóa tất cả các quyền cũ của User này trong bảng Authorities
		List<Authority> oldAuthorities = authorityDAO.findByUser_Id(id);
		if (oldAuthorities != null && !oldAuthorities.isEmpty()) {
			authorityDAO.deleteAll(oldAuthorities);
		}

		// 🟢 Lưu lại toàn bộ các quyền mới vừa tick
		saveUserAuthorities(user, request);

		return convertToResponse(user);
	}

	private void saveUserAuthorities(User user, UserRequest request) {
		List<String> targetRoles = new ArrayList<>();

		if (request.getRoles() != null && !request.getRoles().isEmpty()) {
			targetRoles.addAll(request.getRoles());
		} else if (request.getRole() != null && !request.getRole().isBlank()) {
			targetRoles.add(request.getRole());
		} else {
			targetRoles.add("ROLE_USER");
		}

		for (String roleName : targetRoles) {
			Role role = roleDAO.findByName(roleName)
					.orElseThrow(() -> new RuntimeException("Không tìm thấy Role: " + roleName));

			Authority authority = new Authority();
			authority.setUser(user);
			authority.setRole(role);
			authorityDAO.save(authority);
		}
	}

	@Override
	@Transactional
	public void deleteUser(Long id) {
		if (!userDAO.existsById(id)) {
			throw new RuntimeException("User không tồn tại");
		}
		// Xóa quyền trước khi xóa User
		List<Authority> oldAuthorities = authorityDAO.findByUser_Id(id);
		if (oldAuthorities != null && !oldAuthorities.isEmpty()) {
			authorityDAO.deleteAll(oldAuthorities);
		}
		userDAO.deleteById(id);
	}

	@Override
	@Transactional
	public UserResponse changeStatus(Long id) {
		User user = userDAO.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

		user.setEnabled(!user.getEnabled());
		userDAO.save(user);

		return convertToResponse(user);
	}

	private UserResponse convertToResponse(User user) {
		List<Authority> authorities = authorityDAO.findByUser_Id(user.getId());
		List<String> roleNames = new ArrayList<>();

		if (authorities != null && !authorities.isEmpty()) {
			for (Authority a : authorities) {
				if (a.getRole() != null) {
					roleNames.add(a.getRole().getName());
				}
			}
		}

		if (roleNames.isEmpty()) {
			roleNames.add("ROLE_USER");
		}

		String primaryRole = roleNames.contains("ROLE_ADMIN") ? "ROLE_ADMIN"
				: (roleNames.contains("ROLE_STAFF") ? "ROLE_STAFF" : "ROLE_USER");

		return UserResponse.builder().id(user.getId()).fullName(user.getFullName()).email(user.getEmail())
				.phone(user.getPhone()).address(user.getAddress()).avatar(user.getAvatar()).enabled(user.getEnabled())
				.role(primaryRole).roles(roleNames) // Trả về danh sách tất cả các quyền
				.build();
	}
}