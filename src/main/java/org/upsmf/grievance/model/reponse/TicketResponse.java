package org.upsmf.grievance.model.reponse;

import lombok.Builder;
import lombok.Data;
import org.upsmf.grievance.model.es.Ticket;

import java.util.List;

@Builder
@Data
public class TicketResponse {
    private Long count;
    private List<Ticket> data;
}
