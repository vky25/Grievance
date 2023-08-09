package org.upsmf.grievance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upsmf.grievance.model.OtpRequest;
import org.upsmf.grievance.model.OtpValidationRequest;
import org.upsmf.grievance.service.OtpService;

@RestController
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody OtpRequest otpRequest) {
        try {
            otpService.generateAndSendOtp(otpRequest);
            return ResponseEntity.ok("OTP generated and sent successfully to " + otpRequest.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate and send OTP: " + e.getMessage());
        }
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<String> validateOtp(@RequestBody OtpValidationRequest otpValidationRequest) {
        boolean isValid = otpService.validateOtp(otpValidationRequest.getEmail(), otpValidationRequest.getOtp());
        if (isValid) {
            return ResponseEntity.ok("OTP validation successful.");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP.");
        }
    }


}
