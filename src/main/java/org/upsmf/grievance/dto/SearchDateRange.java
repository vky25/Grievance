package org.upsmf.grievance.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SearchDateRange {
    private Long to;
    private Long from;
}
