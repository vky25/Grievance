package org.upsmf.grievance.enums;

public enum ErrorCode422 {
    UNABLE_TO_FETCH_PROFILE("GVS-422-001","Unable to Fetch Profile"),
    NO_PROFILE_FOUND("GVS-422-002","No Profile Found");

    private String code;
    private String message;

    ErrorCode422(String code, String message) {
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
