package org.upsmf.grievance.service;

import org.upsmf.grievance.dto.SearchRequest;
import org.upsmf.grievance.model.reponse.TicketResponse;

import java.util.Map;

public interface SearchService {
    TicketResponse search(SearchRequest searchRequest);
    Map<String, Object> searchTickets(SearchRequest searchRequest);
}