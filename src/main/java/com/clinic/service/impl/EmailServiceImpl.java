package com.clinic.service.impl;

import com.clinic.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String toEmail, String subject, String body) {
        logger.info("Attempting to send email to: {}", toEmail);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(toEmail);
            helper.setSubject(subject);
            
            boolean isHtml = body.contains("<html") || body.contains("<!DOCTYPE html>");
            helper.setText(body, isHtml);
            helper.setFrom("chathumik2004@gmail.com");
            
            mailSender.send(mimeMessage);
            logger.info("Successfully sent email to: {}", toEmail);
            System.out.println("\n[EMAIL SUCCESS] Email sent successfully to " + toEmail + "!\n");
        } catch (Exception e) {
            logger.error("Failed to send email (Error: {}). Falling back to console simulation.", e.getMessage());
            System.out.println("\n[EMAIL GATEWAY SIMULATOR - FALLBACK] Sent Email to " + toEmail + " with Subject: \"" + subject + "\"\n");
            logger.info("[EMAIL GATEWAY SIMULATOR - FALLBACK] BODY:\n--------------------------------------------------\n{}\n--------------------------------------------------", body);
        }
    }
}
