package org.upsmf.grievance.exception;

import org.upsmf.grievance.util.ErrorCode;

public class TicketException extends CustomException {

    public TicketException(String message) {
        super(message);
    }

    public TicketException(String message, String description) {
        super(message, description);
    }

    public TicketException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }


    public TicketException(String message, ErrorCode errorCode, String description) {
        super(message, errorCode, description);
    }
}
