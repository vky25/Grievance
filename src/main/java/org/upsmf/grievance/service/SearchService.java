package org.upsmf.grievance.service;

import org.upsmf.grievance.model.es.Ticket;
import org.upsmf.grievance.dto.SearchRequest;
import org.upsmf.grievance.model.reponse.TicketResponse;

import java.util.Optional;

public interface SearchService {
    TicketResponse search(SearchRequest searchRequest);
}