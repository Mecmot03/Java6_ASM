package nhomhoinuong.java6_asm.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import nhomhoinuong.java6_asm.dto.UserRequest;
import nhomhoinuong.java6_asm.dto.UserResponse;
import nhomhoinuong.java6_asm.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ==========================
    // Lấy tất cả User
    // ==========================
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    // ==========================
    // Lấy User theo ID
    // ==========================
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // ==========================
    // Thêm User
    // ==========================
    @PostMapping
    public UserResponse createUser(@RequestBody UserRequest request) {
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
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    // ==========================
    // Khóa / Mở khóa User
    // ==========================
    @PatchMapping("/{id}/status")
    public UserResponse changeStatus(@PathVariable Long id) {
        return userService.changeStatus(id);
    }

}