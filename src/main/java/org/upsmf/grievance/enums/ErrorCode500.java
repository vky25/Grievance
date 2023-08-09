package org.upsmf.grievance.enums;

public enum ErrorCode500 {

    INTERNAL_SERVER_ERROR("GVS-500-999","Internal Technical Error.");

    private String code;
    private String message;

    ErrorCode500(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

