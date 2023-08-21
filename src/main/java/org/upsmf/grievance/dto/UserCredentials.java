package org.upsmf.grievance.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserCredentials {

    private String type;
    private String value;
    private boolean temporary = false;
}
