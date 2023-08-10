package org.upsmf.grievance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.upsmf.grievance.dto.SearchRequest;
import org.upsmf.grievance.model.reponse.Response;
import org.upsmf.grievance.model.reponse.TicketResponse;
import org.upsmf.grievance.service.SearchService;

import java.util.Map;

@Controller
@RequestMapping("/search")
public class SearchTicketController {

    @Autowired
    private SearchService searchService;

    @PostMapping("/getAllTickets")
    public ResponseEntity<Response> search(@RequestBody SearchRequest searchRequest) {
        TicketResponse responseTicket = searchService.search(searchRequest);
        Response response = new Response(HttpStatus.OK.value(), responseTicket);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/searchTickets")
    public ResponseEntity<Response> searchTickets(@RequestBody SearchRequest searchRequest) {
        Map<String, Object> responseTicket = searchService.searchTickets(searchRequest);
        Response response = new Response(HttpStatus.OK.value(), responseTicket);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/dashboardReport")
    public ResponseEntity<Response> getDashboardReport(@RequestBody SearchRequest searchRequest) {
        Map<String, Object> responseTicket = searchService.dashboardReport(searchRequest);
        Response response = new Response(HttpStatus.OK.value(), responseTicket);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}