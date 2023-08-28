package org.upsmf.grievance.service.impl;

// Importing required classes
import java.io.File;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.model.EmailDetails;
import org.upsmf.grievance.service.EmailService;

// Annotation
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    // Method 1
    // To send a simple email
    @Override
    public void sendSimpleMail(EmailDetails details)
    {
        // Try block to check for exceptions
        try {
            // Creating a simple mail message
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());
            // Sending the mail
            javaMailSender.send(mailMessage);
            log.info("Mail Sent Successfully...");
        }
        // Catch block to handle the exceptions
        catch (Exception e) {
            log.error("Error while Sending Mail", e);
        }
    }

    // Method 2
    // To send an email with attachment
    @Override
    public void sendMailWithAttachment(EmailDetails details)
    {
        // Creating a mime message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            // Setting multipart as true for attachments tobe send
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(details.getSubject());
            // Adding the attachment
            FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));
            mimeMessageHelper.addAttachment(file.getFilename(), file);
            // Sending the mail
            javaMailSender.send(mimeMessage);
            log.info("Mail Sent Successfully...");
        }
        // Catch block to handle MessagingException
        catch (MessagingException e) {
            // Display message when exception occurred
            log.error("Error while Sending Mail with attachment", e);
        }
    }
}
