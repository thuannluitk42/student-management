package com.thuannluit.student_management.service;

public interface EmailService {
    void sendWelcomeEmail(String to, String email);
    String buildWelcomeHtml(String email);
}
