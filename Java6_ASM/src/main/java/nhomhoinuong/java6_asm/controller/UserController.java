package nhomhoinuong.java6_asm.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import nhomhoinuong.java6_asm.dto.UserRequest;
import nhomhoinuong.java6_asm.dto.UserResponse;
import nhomhoinuong.java6_asm.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
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
	// Xóa vĩnh viễn User
	// ==========================
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		try {
			userService.deleteUser(id);
			return ResponseEntity.ok("Xóa tài khoản User thành công!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Lỗi khi xóa User: " + e.getMessage());
		}
	}

	// ==========================
	// Khóa / Mở khóa User
	// ==========================
	@PatchMapping("/{id}/status")
	public UserResponse changeStatus(@PathVariable Long id) {
		return userService.changeStatus(id);
	}

}