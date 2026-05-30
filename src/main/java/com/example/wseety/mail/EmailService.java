package com.example.wseety.mail;

import com.example.wseety.otp.OtpProperties;
import com.example.wseety.otp.OtpType;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {







    final private OtpProperties otpProperties ;
    final private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(this.otpProperties.getSender().getEmail()); // نفس البريد الموجود في Mailgun
        mailSender.send(message);
    }

    // Specialized method for OTP emails
    public void sendOtpEmail(String to, String otpCode, OtpType type) {
        String subject = getOtpSubject(type);
        String body = buildOtpEmailBody(to, otpCode, type);
        sendEmail(to, subject, body);
    }

    private String buildOtpEmailBody(String to, String otpCode, OtpType type) {
        String actionText = getActionText(type);
        String buttonText = getButtonText(type);

        return String.format(
                """
                ========================================
                        VERIFICATION CODE (OTP)
                ========================================
                
                Dear User,
                
                %s
                
                Your verification code is:
                
                🔐 %s 🔐
                
                This code is valid for %d minutes.
                For security reasons, do not share this code with anyone.
                
                If you did not request this code, please ignore this email.
                
                Best regards,
                Your Support Team
                ========================================
                
                Need help? Contact our support team.
                """,
                actionText,
                otpCode,
                this.otpProperties.getExpiryMinutes()
        );
    }

    private String getActionText(OtpType type) {
        return switch (type) {
            case REGISTRATION ->
                    "You have requested to register a new account. " +
                            "Please use the following verification code to complete your registration:";

            case PASSWORD_RESET ->
                    "We received a request to reset your password. " +
                            "Please use the following verification code to proceed:";

            case LOGIN_VERIFICATION ->
                    "A login attempt was detected from a new device. " +
                            "Please use the following verification code to verify your identity:";
        };
    }

    private String getButtonText(OtpType type) {
        return switch (type) {
            case  REGISTRATION -> "Complete Registration";
            case  PASSWORD_RESET -> "Reset Password";
            case  LOGIN_VERIFICATION -> "Verify Login";
        };
    }

    private String getOtpSubject(OtpType type) {
        return switch (type) {
            case REGISTRATION -> "🔐 Email Verification Code - Complete Your Registration";
            case PASSWORD_RESET -> "🔐 Password Reset Verification Code";
            case LOGIN_VERIFICATION -> "🔐 Login Verification Code - Action Required";
        };
    }
}
