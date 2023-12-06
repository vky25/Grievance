package org.upsmf.grievance.exception;

import org.upsmf.grievance.util.ErrorCode;

public class OtpException extends CustomException {

    public OtpException(String message) {
        super(message);
    }

    public OtpException(String message, String description) {
        super(message, description);
    }

    public OtpException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }


    public OtpException(String message, ErrorCode errorCode, String description) {
        super(message, errorCode, description);
    }
}
