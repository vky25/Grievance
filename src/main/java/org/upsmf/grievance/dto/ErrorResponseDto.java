package org.upsmf.grievance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

    private int statusCode;
    private String message;

    public ErrorResponseDto(String message)
    {
        super();
        this.message = message;
    }
}