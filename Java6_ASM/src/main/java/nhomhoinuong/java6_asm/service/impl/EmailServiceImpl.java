package nhomhoinuong.java6_asm.service.impl;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import nhomhoinuong.java6_asm.bean.Order;
import nhomhoinuong.java6_asm.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    @Async
    public void sendOrderInvoice(String toEmail, Order order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("XÁC NHẬN ĐƠN HÀNG #" + order.getId() + " - Thế Giới Điện Tử");

            String htmlMsg = "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; max-width: 600px; margin: 0 auto;'>"
                    + "<h2 style='color: #ffc107; text-align: center;'>CẢM ƠN BẠN ĐÃ ĐẶT HÀNG!</h2>"
                    + "<p>Xin chào <strong>" + order.getReceiverName() + "</strong>,</p>"
                    + "<p>Đơn hàng của bạn đã được hệ thống ghi nhận thành công. Dưới đây là thông tin hóa đơn:</p>"
                    + "<hr style='border: none; border-top: 1px dashed #ccc;'/>"
                    + "<p><strong>Mã đơn hàng:</strong> #" + order.getId() + "</p>"
                    + "<p><strong>Số điện thoại:</strong> " + order.getReceiverPhone() + "</p>"
                    + "<p><strong>Địa chỉ giao hàng:</strong> " + order.getShippingAddress() + "</p>"
                    + "<p><strong>Phương thức thanh toán:</strong> " + order.getPaymentMethod() + "</p>"
                    + "<h3 style='color: #d9534f; background: #fdf7f7; padding: 10px; border-radius: 5px;'>Tổng thanh toán: " 
                    + String.format("%,.0f VNĐ", order.getTotalAmount()) + "</h3>"
                    + "<hr style='border: none; border-top: 1px dashed #ccc;'/>"
                    + "<p style='color: #666; font-size: 13px;'>Mọi thắc mắc vui lòng liên hệ bộ phận CSKH 24/7 của Thế Giới Điện Tử.</p>"
                    + "</div>";

            helper.setText(htmlMsg, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Lỗi gửi email hóa đơn: " + e.getMessage());
        }
    }
}