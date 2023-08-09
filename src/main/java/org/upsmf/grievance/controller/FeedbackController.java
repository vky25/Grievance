package org.upsmf.grievance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.upsmf.grievance.dto.FeedbackDto;
import org.upsmf.grievance.model.reponse.Response;
import org.upsmf.grievance.service.FeedbackService;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/save")
    public ResponseEntity saveFeedback(@RequestBody FeedbackDto feedbackDto) {
        feedbackService.saveFeedback(feedbackDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getFeedbacks() {
        return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(), feedbackService.getFeedbacks()), HttpStatus.OK);
    }
}
