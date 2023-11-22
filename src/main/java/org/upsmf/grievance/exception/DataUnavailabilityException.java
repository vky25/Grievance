package org.upsmf.grievance.exception;

import org.upsmf.grievance.util.ErrorCode;

public class DataUnavailabilityException extends CustomException {

    public DataUnavailabilityException(String message) {
        super(message);
    }

    public DataUnavailabilityException(String message, String description) {
        super(message, description);
    }

    public DataUnavailabilityException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }


    public DataUnavailabilityException(String message, ErrorCode errorCode, String description) {
        super(message, errorCode, description);
    }
}
