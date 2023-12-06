package org.upsmf.grievance.exception;

import org.upsmf.grievance.util.ErrorCode;

public class UserException extends CustomException {

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, String description) {
        super(message, description);
    }

    public UserException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }


    public UserException(String message, ErrorCode errorCode, String description) {
        super(message, errorCode, description);
    }
}
