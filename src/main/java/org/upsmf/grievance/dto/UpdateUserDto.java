package org.upsmf.grievance.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UpdateUserDto {

    private Long id;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private Map<String, String> attributes;
    private List<UserCredentials> credentials;
    private boolean enabled = true;
}
