	package nhomhoinuong.java6_asm.controller;
	
	import java.util.List;
	
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.web.bind.annotation.*;
	
	import nhomhoinuong.java6_asm.bean.User;
	import nhomhoinuong.java6_asm.service.UserService;
	
	@RestController
	@RequestMapping("/api/users")
	public class UserController {
	
	    @Autowired
	    private UserService userService;
	
	    // Lấy danh sách User
	    @GetMapping
	    public List<User> getAllUsers() {
	        return userService.findAll();
	    }
	
	    // Lấy User theo Id
	    @GetMapping("/{id}")
	    public User getUser(@PathVariable Long id) {
	        return userService.findById(id).orElse(null);
	    }
	
	}