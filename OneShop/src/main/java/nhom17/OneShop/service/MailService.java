package nhom17.OneShop.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * Service hợp nhất cho việc gửi email.
 * Hỗ trợ gửi cả email dạng text đơn giản và dạng HTML.
 * Xử lý lỗi chi tiết để dễ dàng gỡ rối.
 */
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender mailSender;
    private final String fromEmail;

    /**
     * Tên người gửi sẽ hiển thị trong hòm thư của người nhận.
     */
    private final String senderName = "OneShop";

    public MailService(JavaMailSender mailSender, @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    /**
     * Gửi một email với nội dung dạng HTML.
     *
     * @param to      Địa chỉ email người nhận.
     * @param subject Tiêu đề của email.
     * @param htmlBody  Nội dung email dưới dạng chuỗi HTML.
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        log.info("Attempting to send HTML email to '{}' with subject '{}'", to, subject);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(fromEmail, senderName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true để chỉ định đây là nội dung HTML
            mailSender.send(mimeMessage);
            log.info("Successfully sent HTML email to '{}'", to);
        } catch (MailAuthenticationException e) {
            log.error("Authentication failed while sending email to '{}'. Check your App Password.", to, e);
            throw new RuntimeException("Lỗi xác thực Gmail. Vui lòng kiểm tra lại App Password.", e);
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("Failed to create or send email message to '{}'", to, e);
            throw new RuntimeException("Không thể tạo hoặc gửi email.", e);
        } catch (MailException e) {
            log.error("A mail-related error occurred while sending to '{}'", to, e);
            throw new RuntimeException("Gửi email thất bại.", e);
        }
    }

    /**
     * Gửi một email với nội dung dạng văn bản thuần (plain text).
     *
     * @param to      Địa chỉ email người nhận.
     * @param subject Tiêu đề của email.
     * @param textBody  Nội dung email.
     */
    public void sendSimpleEmail(String to, String subject, String textBody) {
        log.info("Attempting to send simple text email to '{}' with subject '{}'", to, subject);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail); // Đối với SimpleMailMessage, không set được senderName
            message.setTo(to);
            message.setSubject(subject);
            message.setText(textBody);
            mailSender.send(message);
            log.info("Successfully sent simple text email to '{}'", to);
        } catch (MailAuthenticationException e) {
            log.error("Authentication failed while sending email to '{}'. Check your App Password.", to, e);
            throw new RuntimeException("Lỗi xác thực Gmail. Vui lòng kiểm tra lại App Password.", e);
        } catch (MailException e) {
            log.error("A mail-related error occurred while sending to '{}'", to, e);
            throw new RuntimeException("Gửi email thất bại.", e);
        }
    }
}
