package org.upsmf.grievance.service.impl;

// Importing required classes

import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.model.EmailDetails;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.service.EmailService;
import org.upsmf.grievance.util.DateUtil;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.StringWriter;

// Annotation
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void sendCreateTicketMail(EmailDetails details, Ticket ticket) {
        // Try block to check for exceptions
        try {
            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                    message.setTo(details.getRecipient());
                    message.setSubject(details.getSubject());

                    VelocityContext velocityContext = new VelocityContext();
                    velocityContext.put("first_name", ticket.getFirstName());
                    velocityContext.put("id", ticket.getId());
                    velocityContext.put("created_date", DateUtil.getFormattedDateInString(ticket.getCreatedDate()));
                    // signature
                    velocityContext.put("signature_name", "U.P. State Medical Faculty");
                    velocityContext.put("address", "Address: 5, Sarvpalli, Mall Avenue Road,  Lucknow - 226001 (U.P.) India");
                    velocityContext.put("phone", "Phone: (0522) 2238846, 2235964, 2235965, 3302100");
                    velocityContext.put("mobile", "Mobile : +91-8400955546 / +91-9151024463");
                    velocityContext.put("fax", "Fax : (0522) 2236600");
                    velocityContext.put("email", "Email:  upmedicalfaculty@upsmfac.org");

                    StringWriter stringWriter = new StringWriter();
                    velocityEngine.mergeTemplate("templates/raiser-create-ticket.vm", "UTF-8", velocityContext, stringWriter);

                    message.setText(stringWriter.toString(), true);
                }
            };
            // Sending the mail
            javaMailSender.send(preparator);
            log.info("create ticket mail Sent Successfully...");
        }
        // Catch block to handle the exceptions
        catch (Exception e) {
            log.error("Error while Sending Mail", e);
        }
    }

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
