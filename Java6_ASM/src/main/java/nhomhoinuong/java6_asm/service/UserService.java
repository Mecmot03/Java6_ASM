package nhomhoinuong.java6_asm.service;

import java.util.List;

import nhomhoinuong.java6_asm.dto.UserRequest;
import nhomhoinuong.java6_asm.dto.UserResponse;

public interface UserService {

    // Lấy tất cả User
    List<UserResponse> getAllUsers();

    // Lấy User theo ID
    UserResponse getUserById(Long id);

    // Thêm User
    UserResponse createUser(UserRequest request);

    // Cập nhật User
    UserResponse updateUser(Long id, UserRequest request);

    // Xóa User
    void deleteUser(Long id);

    // Khóa / Mở khóa User
    UserResponse changeStatus(Long id);

}