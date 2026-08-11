package nhomhoinuong.java6_asm.service;

import nhomhoinuong.java6_asm.bean.Order;

public interface EmailService {
    void sendOrderInvoice(String toEmail, Order order);
}