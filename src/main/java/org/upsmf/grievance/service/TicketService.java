package org.upsmf.grievance.service;

import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.dto.TicketRequest;
import org.upsmf.grievance.dto.UpdateTicketRequest;

import javax.transaction.Transactional;

public interface TicketService {

    Ticket save(Ticket ticket);

    Ticket save(TicketRequest ticketRequest) throws Exception;

    Ticket update(UpdateTicketRequest updateTicketRequest) throws Exception;

    Ticket getTicketById(long id);

    @Transactional
    void updateTicket(Long ticketId);
}
