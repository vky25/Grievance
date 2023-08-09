package org.upsmf.grievance.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(includeFieldNames = true)
public class FeedbackDto {

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Integer rating;
    private String comment;
}
