package org.upsmf.grievance.dto;

import lombok.*;
import org.upsmf.grievance.enums.TicketPriority;
import org.upsmf.grievance.enums.TicketStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(includeFieldNames = true)
public class UpdateTicketRequest {

    private long id;
    private long requestedBy;
    private TicketStatus status;
    private Long cc;//AssignedTo
    private TicketPriority priority;
    private String comment;
    private List<String> assigneeAttachmentURLs;
    private Boolean isJunk;
    //nodal office -
    // mark as resolved - status - closed,
    // others cc = -1
    // junk - update status as closed, isJunk
    // attachment url -
}
