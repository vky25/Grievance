package org.upsmf.grievance.enums;

public enum ErrorCode400 {
    INVALID_REQUEST("GVS-400-001","Invalid Request"),
    MISSING_AUTH_TOKEN("GVS-400-002","Missing Auth Token"),
    UNAUTHORIZED_REQUEST("GVS-400-003","Invalid Access Token"),
    INVALID_REFRESH_TOKEN("GVS-400-004","Invalid Refresh Token");

    private String code;
    private String message;

    ErrorCode400(String code, String message) {
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
