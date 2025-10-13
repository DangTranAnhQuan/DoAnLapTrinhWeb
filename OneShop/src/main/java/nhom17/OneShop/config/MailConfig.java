package nhom17.OneShop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

// MailConfig.java
@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl m = new JavaMailSenderImpl();
        m.setHost("smtp.gmail.com");
        m.setPort(587);
        m.setUsername("sponeshop99@gmail.com"); // gmail chủ shop
        m.setPassword("admin123");        // Mật khẩu ứng dụng (không dùng mật khẩu thường)

        Properties props = m.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false");
        return m;
    }
}

