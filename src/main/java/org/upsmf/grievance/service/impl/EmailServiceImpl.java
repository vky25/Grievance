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
import org.upsmf.grievance.enums.Department;
import org.upsmf.grievance.model.AssigneeTicketAttachment;
import org.upsmf.grievance.model.EmailDetails;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.User;
import org.upsmf.grievance.repository.DepartmentRepository;
import org.upsmf.grievance.repository.UserRepository;
import org.upsmf.grievance.repository.UserRoleRepository;
import org.upsmf.grievance.service.EmailService;
import org.upsmf.grievance.util.DateUtil;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

// Annotation
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${site.url}")
    private String siteUrl;

    @Override
    public void sendCreateTicketMail(EmailDetails details, Ticket ticket) {
        // Try block to check for exceptions
        sendMailToRaiser(details, ticket);
        sendMailToAdmin(details, ticket);
        sendMailToNodalOfficer(details, ticket);
    }

    @Override
    public void sendUpdateTicketMail(EmailDetails details, Ticket ticket) {
        // Try block to check for exceptions
        sendUpdateMailToRaiser(details, ticket);
    }

    @Override
    public void sendClosedTicketMail(EmailDetails details, Ticket ticket, String comment, List<AssigneeTicketAttachment> attachments, String feedbackURL) {
        // Try block to check for exceptions
        sendFeedbackMailToRaiser(details, ticket, comment, attachments, feedbackURL);
    }


    private void sendFeedbackMailToRaiser(EmailDetails details, Ticket ticket,
                                         String comment, List<AssigneeTicketAttachment> attachments,
                                         String feedbackUrl) {
        try {
            MimeMessagePreparator preparator = new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                    message.setTo(details.getRecipient());
                    message.setSubject(details.getSubject());

                    List<Department> departmentList = Department.getById(Integer.parseInt(ticket.getAssignedToId()));
                    VelocityContext velocityContext = new VelocityContext();
                    velocityContext.put("first_name", ticket.getFirstName());
                    velocityContext.put("id", ticket.getId());
                    velocityContext.put("created_date", DateUtil.getFormattedDateInString(ticket.getCreatedDate()));
                    velocityContext.put("department", departmentList!=null&&!departmentList.isEmpty()?departmentList.get(0).getCode():"Others");
                    velocityContext.put("comment", comment);
                    velocityContext.put("url", feedbackUrl);

                    // signature
                    createCommonMailSignature(velocityContext);
                    // merge mail body
                    StringWriter stringWriter = new StringWriter();
                    velocityEngine.mergeTemplate("templates/raiser_feedback.vm", "UTF-8", velocityContext, stringWriter);

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

    private void sendUpdateMailToRaiser(EmailDetails details, Ticket ticket) {
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
                    velocityContext.put("status", ticket.getStatus().name());
                    velocityContext.put("updated_date", DateUtil.getFormattedDateInString(ticket.getUpdatedDate()));
                    // signature
                    createCommonMailSignature(velocityContext);
                    // merge mail body
                    StringWriter stringWriter = new StringWriter();
                    velocityEngine.mergeTemplate("templates/raiser_update_ticket.vm", "UTF-8", velocityContext, stringWriter);

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

    private void sendMailToNodalOfficer(EmailDetails details, Ticket ticket) {
        try {

            List<User> users = getUsersByDepartment(ticket.getAssignedToId());
            if(users == null || users.isEmpty()) {
                return;
            }
            users.stream().forEach(x -> {
                MimeMessagePreparator preparator = new MimeMessagePreparator() {
                    public void prepare(MimeMessage mimeMessage) throws Exception {
                        MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                        message.setTo(x.getEmail());
                        message.setSubject(details.getSubject());

                        List<Department> departmentList = Department.getById(Integer.parseInt(ticket.getAssignedToId()));
                        VelocityContext velocityContext = new VelocityContext();
                        velocityContext.put("first_name", x.getFirstName());
                        velocityContext.put("id", ticket.getId());
                        velocityContext.put("created_date", DateUtil.getFormattedDateInString(ticket.getCreatedDate()));
                        velocityContext.put("priority", ticket.getPriority());
                        velocityContext.put("department", departmentList != null && !departmentList.isEmpty() ? departmentList.get(0).getCode() : "Others");
                        velocityContext.put("status", ticket.getStatus().name());
                        velocityContext.put("site_url", siteUrl);
                        // signature
                        createCommonMailSignature(velocityContext);
                        // merge mail body
                        StringWriter stringWriter = new StringWriter();
                        velocityEngine.mergeTemplate("templates/nodal_create_ticket.vm", "UTF-8", velocityContext, stringWriter);

                        message.setText(stringWriter.toString(), true);
                    }
                };
                // Sending the mail
                javaMailSender.send(preparator);
                log.info("create ticket mail Sent Successfully...");
            });
        }
        // Catch block to handle the exceptions
        catch (Exception e) {
            log.error("Error while Sending Mail", e);
        }
    }

    private List<User> getUsersByDepartment(String assignedToId) {
        List<Department> departmentList = Department.getById(Integer.parseInt(assignedToId));
        if(departmentList != null && !departmentList.isEmpty()) {
            String departmentName = departmentList.get(0).getCode();
            List<org.upsmf.grievance.model.Department> userDepartments = departmentRepository.findAllByDepartmentName(departmentName);
            if(userDepartments != null && !userDepartments.isEmpty()){
                List<Long> userIds = new ArrayList<>();
                userDepartments.stream().forEach(x -> userIds.add(x.getUserId()));
                if(!userIds.isEmpty()) {
                    List<User> users = new ArrayList<>();
                    userIds.stream().forEach(x -> {
                        Optional<User> fetchedUser = userRepository.findById(x);
                        if(fetchedUser.isPresent()) {
                            users.add(fetchedUser.get());
                        }
                    });
                    return users;
                }
            }
        }
        return Collections.emptyList();
    }

    private void sendMailToAdmin(EmailDetails details, Ticket ticket) {
        try {
            List<User> users = getUsersByDepartment(String.valueOf(-1));
            if(users == null || users.isEmpty()) {
                return;
            }
            users.stream().forEach(x -> {
                 MimeMessagePreparator preparator = new MimeMessagePreparator() {
                    public void prepare(MimeMessage mimeMessage) throws Exception {
                        MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                        message.setTo(x.getEmail());
                        message.setSubject(details.getSubject());
                        List<Department> departmentList = Department.getById(Integer.parseInt(ticket.getAssignedToId()));
                        VelocityContext velocityContext = new VelocityContext();
                        velocityContext.put("first_name", x.getFirstName());
                        velocityContext.put("id", ticket.getId());
                        velocityContext.put("created_date", DateUtil.getFormattedDateInString(ticket.getCreatedDate()));
                        velocityContext.put("priority", ticket.getPriority());
                        velocityContext.put("department", departmentList != null && !departmentList.isEmpty() ? departmentList.get(0).getCode() : "Others");
                        velocityContext.put("status", ticket.getStatus().name());
                        // signature
                        createCommonMailSignature(velocityContext);
                        // merge mail body
                        StringWriter stringWriter = new StringWriter();
                        velocityEngine.mergeTemplate("templates/admin_create_ticket.vm", "UTF-8", velocityContext, stringWriter);

                        message.setText(stringWriter.toString(), true);
                    }
                };
                // Sending the mail
                javaMailSender.send(preparator);
                log.info("create ticket mail Sent Successfully...");
            });
        }
        // Catch block to handle the exceptions
        catch (Exception e) {
            log.error("Error while Sending Mail", e);
        }
    }

    private static void createCommonMailSignature(VelocityContext velocityContext) {
        velocityContext.put("signature_name", "U.P. State Medical Faculty");
        velocityContext.put("address", "Address: 5, Sarvpalli, Mall Avenue Road,  Lucknow - 226001 (U.P.) India");
        velocityContext.put("phone", "Phone: (0522) 2238846, 2235964, 2235965, 3302100");
        velocityContext.put("mobile", "Mobile : +91-8400955546 / +91-9151024463");
        velocityContext.put("fax", "Fax : (0522) 2236600");
        velocityContext.put("email", "Email:  upmedicalfaculty@upsmfac.org");
    }

    private void sendMailToRaiser(EmailDetails details, Ticket ticket) {
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
                    createCommonMailSignature(velocityContext);
                    // merge mail body
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
