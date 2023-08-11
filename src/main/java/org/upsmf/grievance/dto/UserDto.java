package org.upsmf.grievance.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDto {

    private String userId;
    private String firstName;
    private String userName;

    private String email;
    private String password;
    private boolean emailVerified;
    private String[] roles;

}
