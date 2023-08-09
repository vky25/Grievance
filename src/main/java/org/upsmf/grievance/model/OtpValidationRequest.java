package org.upsmf.grievance.model;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
public class OtpValidationRequest {

    private String email;
    private String otp;
}
