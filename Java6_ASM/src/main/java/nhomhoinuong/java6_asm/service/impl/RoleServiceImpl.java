package nhomhoinuong.java6_asm.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nhomhoinuong.java6_asm.bean.Role;
import nhomhoinuong.java6_asm.dao.RoleDAO;
import nhomhoinuong.java6_asm.service.RoleService;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleDAO roleDAO;

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleById(Long id) {
        return roleDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Role với ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleByName(String name) {
        return roleDAO.findByName(name)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Role với tên: " + name));
    }
}