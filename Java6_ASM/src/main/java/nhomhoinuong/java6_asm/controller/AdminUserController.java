package nhomhoinuong.java6_asm.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import nhomhoinuong.java6_asm.dto.UserRequest;
import nhomhoinuong.java6_asm.dto.UserResponse;
import nhomhoinuong.java6_asm.service.UserService;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AdminUserController {

    private final UserService userService;

    // ==========================
    // Lấy danh sách User
    // ==========================
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    // ==========================
    // Lấy User theo ID
    // ==========================
    @GetMapping("/{id}")
    public UserResponse getUserById(
            @PathVariable Long id) {

        return userService.getUserById(id);

    }

    // ==========================
    // Thêm User
    // ==========================
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @RequestBody UserRequest request) {

        return userService.createUser(request);

    }

    // ==========================
    // Cập nhật User
    // ==========================
    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest request) {

        return userService.updateUser(id, request);

    }

    // ==========================
    // Xóa User
    // ==========================
    @DeleteMapping("/{id}")
    public void deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

    }

    // ==========================
    // Khóa / Mở khóa User
    // ==========================
    @PatchMapping("/{id}/status")
    public UserResponse changeStatus(
            @PathVariable Long id) {

        return userService.changeStatus(id);

    }

}