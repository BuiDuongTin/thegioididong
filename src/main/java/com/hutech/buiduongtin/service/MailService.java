package com.hutech.buiduongtin.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendOtp(String to, String subject, String text) {
        if (mailSender != null) {
            try {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(to);
                msg.setSubject(subject);
                msg.setText(text);
                mailSender.send(msg);
                logger.info("Sent OTP email to {}", to);
                return;
            } catch (Exception e) {
                logger.error("Error sending email", e);
            }
        }
        // Fallback: log OTP (useful for dev environments)
        logger.info("[DEV-FALLBACK] To: {} | Subject: {} | Text: {}", to, subject, text);
    }
}
