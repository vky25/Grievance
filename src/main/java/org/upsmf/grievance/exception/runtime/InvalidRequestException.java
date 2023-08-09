package org.upsmf.grievance.exception.runtime;

public class InvalidRequestException extends RuntimeException {

    private String message;

    public InvalidRequestException() {
    }

    public InvalidRequestException(String msg) {
        super(msg);
        this.message = msg;
    }
}
