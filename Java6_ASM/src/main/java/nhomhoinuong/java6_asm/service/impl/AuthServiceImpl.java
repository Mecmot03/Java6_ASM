package nhomhoinuong.java6_asm.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nhomhoinuong.java6_asm.bean.Authority;
import nhomhoinuong.java6_asm.dao.AuthorityDAO;
import nhomhoinuong.java6_asm.service.AuthService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthorityDAO authorityDAO;

    @Override
    public List<Authority> findAll() {
        return authorityDAO.findAll();
    }

    @Override
    public List<Authority> findByUserId(Long userId) {
        return authorityDAO.findByUserId(userId);
    }

    @Override
    public Authority save(Authority authority) {
        return authorityDAO.save(authority);
    }

    @Override
    public void delete(Long id) {
        authorityDAO.deleteById(id);
    }

}