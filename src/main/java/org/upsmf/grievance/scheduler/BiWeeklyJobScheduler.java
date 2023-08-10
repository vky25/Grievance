package org.upsmf.grievance.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upsmf.grievance.controller.SearchTicketController;
import org.upsmf.grievance.dto.SearchRequest;
import org.upsmf.grievance.model.reponse.Response;
import org.upsmf.grievance.model.reponse.TicketResponse;

@Component
public class BiWeeklyJobScheduler {

    @Autowired
    private SearchTicketController searchTicketController;
    @Scheduled(cron = "0 0 0 */14 * ?")
    public void runBiWeeklyJob(){
        SearchRequest searchRequest = new SearchRequest();
        ResponseEntity<Response> ticketResponse = searchTicketController.search(searchRequest);
    }
}

