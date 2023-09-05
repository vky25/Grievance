package org.upsmf.grievance.service;

import org.upsmf.grievance.model.AssigneeTicketAttachment;
import org.upsmf.grievance.model.EmailDetails;
import org.upsmf.grievance.model.Ticket;

import java.util.List;

public interface EmailService {

    // Method
    // To send a simple email
    void sendCreateTicketMail(EmailDetails details, Ticket ticket);

    void sendUpdateTicketMail(EmailDetails details, Ticket ticket);

    void sendClosedTicketMail(EmailDetails details, Ticket ticket, String comment, List<AssigneeTicketAttachment> attachments, String feedbackURL);

    void sendSimpleMail(EmailDetails details);

    // Method
    // To send an email with attachment
    void sendMailWithAttachment(EmailDetails details);
}
