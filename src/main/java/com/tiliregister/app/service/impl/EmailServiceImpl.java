package com.tiliregister.app.service.impl;

import com.tiliregister.app.model.EmailDomain;
import com.tiliregister.app.service.EmailDomainService;
import com.tiliregister.app.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailDomainService emailDomainService;

    public void sendEmail(String to, String subject, String body) {
        EmailDomain emailDomain = emailDomainService.getEmailDomainById(1L);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailDomain.getHost());
        mailSender.setPort(Integer.parseInt(emailDomain.getPort()));
        mailSender.setUsername(emailDomain.getUsername());
        mailSender.setPassword(emailDomain.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailDomain.getUsername());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}