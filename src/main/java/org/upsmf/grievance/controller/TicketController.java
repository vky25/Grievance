package org.upsmf.grievance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.upsmf.grievance.dto.TicketRequest;
import org.upsmf.grievance.dto.UpdateTicketRequest;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.model.reponse.Response;
import org.upsmf.grievance.service.AttachmentService;
import org.upsmf.grievance.service.TicketService;

@Controller
@RequestMapping("/api/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private AttachmentService attachmentService;

    @PostMapping("/save")
    public ResponseEntity<Ticket> save(@RequestBody TicketRequest ticketRequest) {
        Ticket responseTicket = null;
        try {
            responseTicket = ticketService.save(ticketRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        Response response = new Response(HttpStatus.OK.value(), responseTicket);
        return new ResponseEntity<>(responseTicket, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Response> update(@RequestBody UpdateTicketRequest updateTicketRequest) {
        Ticket responseTicket = null;
        try {
            responseTicket = ticketService.update(updateTicketRequest);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        Response response = new Response(HttpStatus.OK.value(), responseTicket);
        return new ResponseEntity<Response>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> update(@RequestParam(name = "id") long id) {
        Ticket responseTicket = ticketService.getTicketById(id);
        Response response = new Response(HttpStatus.OK.value(), responseTicket);
        return new ResponseEntity<Response>(response, HttpStatus.OK);
    }

    @PostMapping("/file/upload")
    public ResponseEntity upload(@RequestParam("file") MultipartFile file) {
        try {
            return attachmentService.uploadObject(file);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getLocalizedMessage());
        }
    }
}
