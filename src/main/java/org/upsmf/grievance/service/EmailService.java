package org.upsmf.grievance.service;

import org.upsmf.grievance.model.EmailDetails;
import org.upsmf.grievance.model.Ticket;

public interface EmailService {

    // Method
    // To send a simple email
    void sendCreateTicketMail(EmailDetails details, Ticket ticket);

    void sendSimpleMail(EmailDetails details);

    // Method
    // To send an email with attachment
    void sendMailWithAttachment(EmailDetails details);
}
