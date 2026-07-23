package nhomhoinuong.java6_asm.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nhomhoinuong.java6_asm.bean.Authority;
import nhomhoinuong.java6_asm.bean.User;
import nhomhoinuong.java6_asm.dao.AuthorityDAO;
import nhomhoinuong.java6_asm.dao.UserDAO;
import nhomhoinuong.java6_asm.dto.UserRequest;
import nhomhoinuong.java6_asm.dto.UserResponse;
import nhomhoinuong.java6_asm.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final AuthorityDAO authorityDAO;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userDAO.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = userDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        return convertToResponse(user);

    }

    @Override
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

        Authority authority = new Authority();
        authority.setUser(savedUser);
        authority.setRole(request.getRole());

        authorityDAO.save(authority);

        return convertToResponse(savedUser);

    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {

        User user = userDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        if (!user.getEmail().equals(request.getEmail())
                && userDAO.existsByEmail(request.getEmail())) {

            throw new RuntimeException("Email đã tồn tại");

        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        if (request.getPassword() != null
                && !request.getPassword().isBlank()) {

            user.setPassword(
                    passwordEncoder.encode(request.getPassword())
            );

        }

        user.setUpdatedAt(LocalDateTime.now());

        userDAO.save(user);

        Authority authority = authorityDAO.findByUser_Id(id)
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));

        authority.setRole(request.getRole());

        authorityDAO.save(authority);

        return convertToResponse(user);

    }

    @Override
    public void deleteUser(Long id) {

        if (!userDAO.existsById(id)) {
            throw new RuntimeException("User không tồn tại");
        }

        userDAO.deleteById(id);

    }

    @Override
    public UserResponse changeStatus(Long id) {

        User user = userDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        user.setEnabled(!user.getEnabled());

        userDAO.save(user);

        return convertToResponse(user);

    }

    private UserResponse convertToResponse(User user) {

        String role = authorityDAO.findByUser_Id(user.getId())
            .map(authority -> authority.getRole())
            .orElse("");

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .avatar(user.getAvatar())
                .enabled(user.getEnabled())
                .role(role)
                .build();

    }

}