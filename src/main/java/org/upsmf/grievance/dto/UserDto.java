package org.upsmf.grievance.dto;

import lombok.*;
import org.upsmf.grievance.model.Department;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDto {

    private String firstName;
    private String lastname;
    private String userName;
    private String phoneNumber;
    private String email;
    private String password;
    private boolean emailVerified;
    private Department department;
    private String[] roles;

}
