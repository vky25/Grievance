package org.upsmf.grievance.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.upsmf.grievance.util.ErrorCode;

@Data
@NoArgsConstructor
public class CustomException extends RuntimeException {

    private String description;

    private ErrorCode errorCode;

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, String description) {
        super(message);
        this.description = description;
    }

    public CustomException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }


    public CustomException(String message, ErrorCode errorCode, String description) {
        super(message);
        this.description = description;
        this.errorCode = errorCode;
    }
}
