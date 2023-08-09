package org.upsmf.grievance.dto;

import lombok.*;
import org.upsmf.grievance.enums.RequesterType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(includeFieldNames = true)
public class TicketRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private RequesterType userType;
    private Long cc;
    private String requestType;
    private String description;
    private List<String> attachmentURls;
    private String otp;

}
