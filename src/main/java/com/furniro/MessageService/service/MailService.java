package com.furniro.MessageService.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender javaMailSender) {
        this.mailSender = javaMailSender;
    }

    @Async
    public void sendMailaActive(String email, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String activationLink = "http://localhost:8080/api/v1/furniro/account/confirm?token=" + token;

            String content = "<h3>Chào " + ",</h3>"
                    + "<p>Vui lòng click vào link bên dưới để kích hoạt tài khoản:</p>"
                    + "<a href='" + activationLink + "'>Kích hoạt ngay</a>";

            helper.setTo(email);
            helper.setSubject("Kích hoạt tài khoản của bạn");
            helper.setText(content, true);

            mailSender.send(message);
            log.info("Email kích hoạt đã được gửi tới: {}", email);

        } catch (MessagingException e) {
            log.error("Lỗi khi gửi mail: {}", e.getMessage());
            throw new RuntimeException("Không thể gửi email kích hoạt.");
        }
    }

    @Async
    public void sendMailOTP(String username, String email, String OPT) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String content = "<h3>Xin chào " + username + ",</h3>"
                    + "<p>Đây là OPT của bạn : " + OPT + "</p>"
                    + "<p> Vui lòng không gửi cho bất kỳ ai </p>";

            helper.setTo(email);
            helper.setSubject("OPT xác thực quên mật khẩu");
            helper.setText(content, true);

            mailSender.send(message);
            log.info("Email đã gửi opt đến người dùng : {}", email);

        } catch (MessagingException e) {
            log.error("Lỗi khi gửi mail: {}", e.getMessage());
            throw new RuntimeException("Không thể gửi email quên mật khẩu.");
        }
    }

}
