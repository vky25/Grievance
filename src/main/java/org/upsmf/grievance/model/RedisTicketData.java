package org.upsmf.grievance.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(includeFieldNames = true)
public class RedisTicketData {

    private String email;
    private Long ticketId;

    private String date;

    private String emailOtp;

    private String mobileOtp;

    private String phoneNumber;


}
