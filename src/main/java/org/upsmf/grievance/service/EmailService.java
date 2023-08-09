package org.upsmf.grievance.service;

import org.upsmf.grievance.model.EmailDetails;

public interface EmailService {

    // Method
    // To send a simple email
    void sendSimpleMail(EmailDetails details);

    // Method
    // To send an email with attachment
    void sendMailWithAttachment(EmailDetails details);
}
