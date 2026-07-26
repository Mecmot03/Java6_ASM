package nhomhoinuong.java6_asm.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nhomhoinuong.java6_asm.bean.Role;
import nhomhoinuong.java6_asm.service.RoleService;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RoleController {

    private final RoleService roleService;

    // ==========================================
    // API: Lấy danh sách tất cả các Role
    // Frontend gọi: axios.get('/api/roles')
    // ==========================================
    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }

    // ==========================================
    // API: Lấy Role theo ID
    // ==========================================
    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }
}