package nhomhoinuong.java6_asm.service;

import java.util.List;
import nhomhoinuong.java6_asm.bean.Role;

public interface RoleService {

    // Lấy tất cả danh sách Role để hiển thị lên Form Checkbox/Select ở Admin
    List<Role> getAllRoles();

    // Lấy thông tin Role theo ID
    Role getRoleById(Long id);

    // Tìm Role theo tên (VD: "ROLE_USER")
    Role getRoleByName(String name);
}