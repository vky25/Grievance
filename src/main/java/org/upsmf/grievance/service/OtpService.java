package org.upsmf.grievance.service;

import org.springframework.stereotype.Service;
import org.upsmf.grievance.model.OtpRequest;

@Service
public interface OtpService {

    String generateAndSendOtp(OtpRequest otpRequest);
    boolean validateOtp(String email, String otp);
    void sendGenericEmail(String email, String subject, String mailBody);

    Boolean generateAndSendMobileOtp(OtpRequest otpRequest);

    boolean validateMobileOtp(String phoneNumber, String enteredOtp);
}
