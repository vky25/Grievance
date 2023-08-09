package org.upsmf.grievance.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("ticketRepository")
public interface TicketRepository extends CrudRepository<org.upsmf.grievance.model.Ticket, Long> {
}
