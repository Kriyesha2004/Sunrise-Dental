package com.clinic.service.impl;

import com.clinic.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
        logger.info("Attempting to send real email to: {}", toEmail);
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(toEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(body);
            mailMessage.setFrom("chathumik2004@gmail.com");
            
            mailSender.send(mailMessage);
            logger.info("Successfully sent real email to: {}", toEmail);
            System.out.println("\n[EMAIL SUCCESS] Real email sent successfully to " + toEmail + "!\n");
        } catch (Exception e) {
            logger.error("Failed to send real email (Error: {}). Falling back to console simulation.", e.getMessage());
            System.out.println("\n[EMAIL GATEWAY SIMULATOR - FALLBACK] Sent Email to " + toEmail + " with Subject: \"" + subject + "\"\n");
            logger.info("[EMAIL GATEWAY SIMULATOR - FALLBACK] BODY:\n--------------------------------------------------\n{}\n--------------------------------------------------", body);
        }
    }
}
