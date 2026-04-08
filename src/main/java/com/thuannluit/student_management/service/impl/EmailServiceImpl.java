package com.thuannluit.student_management.service.impl;

import com.thuannluit.student_management.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Override
    public void sendWelcomeEmail(String to, String email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Welcome to Student Management!");
            helper.setFrom(fromEmail);

            String htmlContent = buildWelcomeHtml(email);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Welcome email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to {}", to, e);
        }
    }

    @Override
    public String buildWelcomeHtml(String email) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                            padding: 30px;
                            font-family: Arial, sans-serif;
                            color: #333;
                        }
                        .header {
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            padding: 30px;
                            text-align: center;
                            color: white;
                        }
                        .header h1 { margin: 0; font-size: 28px; }
                        .content {
                            padding: 30px;
                            background: #f8f9fa;
                        }
                        .content p { line-height: 1.6; }
                        .btn {
                            display: inline-block;
                            padding: 12px 30px;
                            background: #667eea;
                            color: white;
                            text-decoration: none;
                            border-radius: 5px;
                            margin-top: 20px;
                        }
                        .footer {
                            text-align: center;
                            padding: 20px;
                            font-size: 12px;
                            color: #999;
                        }
                        .email-label {
                            background: #e9ecef;
                            padding: 8px 15px;
                            border-radius: 4px;
                            display: inline-block;
                            margin: 10px 0;
                            font-weight: bold;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Welcome!</h1>
                        </div>
                        <div class="content">
                            <p>Hello,</p>
                            <p>Your account has been created successfully.</p>
                            <p>Your email: <span class="email-label">%s</span></p>
                            <p>You can now log in to your dashboard to get started.</p>
                            <p style="color: #666; font-size: 13px;">
                                If you did not create this account, please ignore this email.
                            </p>
                        </div>
                        <div class="footer">
                            &copy; 2026 Student Management System. All rights reserved.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(email);
    }
}
