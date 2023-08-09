package org.upsmf.grievance.exception.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.upsmf.grievance.dto.ErrorResponseDto;
import org.upsmf.grievance.exception.runtime.InvalidRequestException;

@ControllerAdvice
public class ExceptionHandler4xxController {

    /**
     * Exception handler for Invalid request exception
     * @param e
     * @return
     */
    @ExceptionHandler(value = InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleInvalidRequestException(InvalidRequestException e) {
        return new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

}
