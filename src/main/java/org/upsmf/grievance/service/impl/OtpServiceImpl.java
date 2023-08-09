package org.upsmf.grievance.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.model.OtpRequest;
import org.upsmf.grievance.model.RedisTicketData;
import org.upsmf.grievance.service.OtpService;

import java.util.concurrent.TimeUnit;

@Service(value = "OtpService")
public class OtpServiceImpl implements OtpService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${otp.expiration.minutes}")
    private int otpExpirationMinutes;

    @Autowired
    public OtpServiceImpl(StringRedisTemplate redisTemplate, JavaMailSender mailSender) {
        this.redisTemplate = redisTemplate;
        this.mailSender = mailSender;
    }

    @Override
    public String generateAndSendOtp(OtpRequest otpRequest) {
        String email = otpRequest.getEmail();
        String otp = generateOtp();
        String mobileOtp= generateOtp();
        RedisTicketData redisTicketData = new RedisTicketData();
        redisTicketData.setEmail(email);
        redisTicketData.setPhoneNumber(otpRequest.getPhone());
        redisTicketData.setEmailOtp(otp);
        redisTicketData.setMobileOtp(mobileOtp);

        String redisKey = "otp:" + email;
        redisTemplate.opsForValue().set(redisKey, toJson(redisTicketData), otpExpirationMinutes, TimeUnit.MINUTES);

        sendOtpEmail(email, otp);
        return otp;
    }

    @Override
    public boolean validateOtp(String email, String enteredOtp) {
        String redisKey = "otp:" + email;
        String redisData = redisTemplate.opsForValue().get(redisKey);
        if (redisData != null) {
            RedisTicketData ticketData = fromJson(redisData, RedisTicketData.class);
            if (ticketData.getEmailOtp().equals(enteredOtp)) {
                // Remove the data from Redis after successful validation
                redisTemplate.delete(redisKey);
                return true;
            }
        }
        return false;
    }

    private String toJson(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // Handle the exception
            return null;
        }
    }

    private <T> T fromJson(String json, Class<T> valueType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            // Handle the exception
            return null;
        }
    }


    private String generateOtp() {
        int otpLength = 6;
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < otpLength; i++) {
            int digit = (int) (Math.random() * 10);
            otp.append(digit);
        }

        return otp.toString();
    }

    private void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP for Ticket Creation");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);
    }

    @Override
    public void sendGenericEmail(String email, String subject, String mailBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(mailBody);
        mailSender.send(message);
    }
}
