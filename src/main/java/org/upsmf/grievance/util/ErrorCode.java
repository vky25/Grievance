package org.upsmf.grievance.util;

public enum ErrorCode {

    OTP_001("Failed OTP - calling external resource to send otp"),
    OTP_002("Invalid sender id - calling external resource to send otp"),
    OTP_003("Invalid channel - calling external resource to send otp"),
    OTP_004("OTP server error - calling external resource to send otp"),
    DATA_001("Data unavailability in Redis server"),
    USER_001("Failed to find user"),
    USER_002("User already exist"),
    USER_003("Failed User creation"),
    OTP_000("Internal OTP error");

    private String description;

    ErrorCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
