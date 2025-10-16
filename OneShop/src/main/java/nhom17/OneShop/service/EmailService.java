package nhom17.OneShop.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp, String purpose);
}