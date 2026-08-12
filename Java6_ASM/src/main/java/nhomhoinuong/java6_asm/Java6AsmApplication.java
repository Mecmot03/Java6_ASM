package nhomhoinuong.java6_asm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync; // 🔴 Thêm import

@SpringBootApplication
@EnableAsync // 🔴 Kích hoạt gửi email chạy ngầm bất đồng bộ
public class Java6AsmApplication {

    public static void main(String[] args) {
        SpringApplication.run(Java6AsmApplication.class, args);
    }
}