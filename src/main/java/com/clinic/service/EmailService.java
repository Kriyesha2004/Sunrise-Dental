package com.clinic.service;

public interface EmailService {
    void sendEmail(String toEmail, String subject, String body);
}
