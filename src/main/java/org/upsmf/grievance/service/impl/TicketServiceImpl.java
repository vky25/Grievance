package org.upsmf.grievance.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.dto.TicketRequest;
import org.upsmf.grievance.dto.UpdateTicketRequest;
import org.upsmf.grievance.enums.TicketPriority;
import org.upsmf.grievance.enums.TicketStatus;
import org.upsmf.grievance.model.AssigneeTicketAttachment;
import org.upsmf.grievance.model.Comments;
import org.upsmf.grievance.model.RaiserTicketAttachment;
import org.upsmf.grievance.model.Ticket;
import org.upsmf.grievance.repository.AssigneeTicketAttachmentRepository;
import org.upsmf.grievance.repository.CommentRepository;
import org.upsmf.grievance.repository.RaiserTicketAttachmentRepository;
import org.upsmf.grievance.repository.es.TicketRepository;
import org.upsmf.grievance.service.OtpService;
import org.upsmf.grievance.service.TicketService;
import org.upsmf.grievance.util.DateUtil;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    @Qualifier("esTicketRepository")
    private TicketRepository esTicketRepository;

    @Autowired
    @Qualifier("ticketRepository")
    private org.upsmf.grievance.repository.TicketRepository ticketRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AssigneeTicketAttachmentRepository assigneeTicketAttachmentRepository;

    @Autowired
    private RaiserTicketAttachmentRepository raiserTicketAttachmentRepository;

    @Autowired
    private OtpService otpService;

    /**
     *
     * @param ticket
     * @return
     */
    @Transactional
    public Ticket saveWithAttachment(Ticket ticket, List<String> attachments) {
        // save ticket in postgres
        org.upsmf.grievance.model.Ticket psqlTicket = ticketRepository.save(ticket);
        // update attachments if present
        if(attachments != null) {
            for(String url : attachments) {
                RaiserTicketAttachment raiserTicketAttachment = RaiserTicketAttachment.builder()
                        .attachment_url(url)
                        .ticketId(ticket.getId())
                        .build();
                raiserTicketAttachmentRepository.save(raiserTicketAttachment);
            }
        }
        // covert to ES ticket object
        org.upsmf.grievance.model.es.Ticket esticket = convertToESTicketObj(ticket);
        // save ticket in ES
        esTicketRepository.save(esticket);
        // TODO send mail
        return psqlTicket;
    }

    @Override
    @Transactional
    public Ticket save(Ticket ticket) {
        // save ticket in postgres
        org.upsmf.grievance.model.Ticket psqlTicket = ticketRepository.save(ticket);
        // covert to ES ticket object
        org.upsmf.grievance.model.es.Ticket esticket = convertToESTicketObj(ticket);
        // save ticket in ES
        esTicketRepository.save(esticket);
        // TODO send mail
        return psqlTicket;
    }

    /**
     *
     * @param ticketRequest
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public Ticket save(TicketRequest ticketRequest) throws Exception {
        // validate request
        validateTicketRequest(ticketRequest);
        // validate OTP
        boolean isValid = otpService.validateOtp(ticketRequest.getEmail(), ticketRequest.getOtp());
        if(!isValid) {
            throw new RuntimeException("Bad Request");
        }
        // set default value for creating ticket
        Ticket ticket = createTicketWithDefault(ticketRequest);
        // create ticket
        ticket = saveWithAttachment(ticket, ticketRequest.getAttachmentURls());
        // TODO get email subject and body from db
        otpService.sendGenericEmail(ticketRequest.getEmail(), "Ticket Created", "You ticket is created with ID "+ticket.getId()+" at "+ticket.getCreatedDate());
        return ticket;
    }

    /**
     *
     * @param ticketRequest
     * @return
     * @throws Exception
     */
    private Ticket createTicketWithDefault(TicketRequest ticketRequest) throws Exception {
        Timestamp currentTimestamp = new Timestamp(DateUtil.getCurrentDate().getTime());
        return Ticket.builder()
                .createdDate(new Timestamp(DateUtil.getCurrentDate().getTime()))
                .firstName(ticketRequest.getFirstName())
                .lastName(ticketRequest.getLastName())
                .phone(ticketRequest.getPhone())
                .email(ticketRequest.getEmail())
                .requesterType(ticketRequest.getUserType())
                .assignedToId(ticketRequest.getCc())
                .description(ticketRequest.getDescription())
                .createdDate(currentTimestamp)
                .updatedDate(currentTimestamp)
                .lastUpdatedBy(-1l)
                .escalated(false)
                .escalatedDate(null)
                .escalatedTo(-1l)
                .status(TicketStatus.OPEN)
                .requestType(ticketRequest.getRequestType())
                .priority(TicketPriority.LOW)
                .escalatedBy(-1l)
                .build();
    }

    /**
     *
     * @param updateTicketRequest
     * @return
     */
    @Override
    @Transactional
    public Ticket update(UpdateTicketRequest updateTicketRequest) {
        //  validate ticket
        validateUpdateTicketRequest(updateTicketRequest);
        // check if the ticket exists
        Optional<Ticket> ticketDetails = getTicketDetailsByID(updateTicketRequest.getId());
        Ticket ticket = null;
        if(!ticketDetails.isPresent()) {
            // TODO throw exception
            throw new RuntimeException("Ticket does not exists");
        }
        ticket = ticketDetails.get();
        // set incoming values
        setUpdateTicket(updateTicketRequest, ticket);
        // update ticket in DB
        ticketRepository.save(ticket);
        ticket = getTicketById(ticket.getId());
        // check if ticket exists in ES
        Optional<org.upsmf.grievance.model.es.Ticket> esTicketDetails = esTicketRepository.findOneByTicketId(updateTicketRequest.getId());
        org.upsmf.grievance.model.es.Ticket updatedESTicket = convertToESTicketObj(ticket);
        if(esTicketDetails.isPresent()) {
            // TODO revisit this
            esTicketRepository.deleteById(esTicketDetails.get().getId());
        }
        org.upsmf.grievance.model.es.Ticket curentUpdatedTicket=esTicketRepository.save(updatedESTicket);
        //send mail to enduser
        // TODO get email subject and body from db
        TicketStatus currentTicketStatus=curentUpdatedTicket.getStatus();
        curentUpdatedTicket.getEmail();
        curentUpdatedTicket.getTicketId();
        otpService.sendGenericEmail(curentUpdatedTicket.getEmail(), "updated Ticket for " +curentUpdatedTicket.getTicketId() ,"ticket as updated to" +curentUpdatedTicket.getStatus().name());
        return ticket;
    }

    @Override
    public Ticket getTicketById(long id) {
        if(id <= 0) {
            throw new RuntimeException("Invalid Ticket ID");
        }
        Optional<Ticket> ticketDetails = getTicketDetailsByID(id);
        if(!ticketDetails.isPresent()) {
            throw new RuntimeException("Invalid Ticket ID");
        }
        return ticketDetails.get();
    }

    /**
     *
     * @param updateTicketRequest
     * @param ticket
     */
    private void setUpdateTicket(UpdateTicketRequest updateTicketRequest, Ticket ticket) {
        // TODO check request role and permission
        if(updateTicketRequest.getStatus()!=null)
            ticket.setStatus(updateTicketRequest.getStatus());
        if(updateTicketRequest.getCc()!=null)
            ticket.setAssignedToId(updateTicketRequest.getCc());
        if(updateTicketRequest.getPriority()!=null)
            ticket.setPriority(updateTicketRequest.getPriority());
        if(updateTicketRequest.getIsJunk()!=null)
            ticket.setJunk(updateTicketRequest.getIsJunk());
        // update assignee comments
        if(updateTicketRequest.getComment()!=null) {
            Comments comments = Comments.builder().comment(updateTicketRequest.getComment())
                    .userId(updateTicketRequest.getRequestedBy())
                    .ticketId(ticket.getId())
                    .build();
            commentRepository.save(comments);
        }
        // update assignee attachment url
        if(updateTicketRequest.getAssigneeAttachmentURLs() != null) {
            for (String url : updateTicketRequest.getAssigneeAttachmentURLs()) {
                AssigneeTicketAttachment assigneeTicketAttachment = AssigneeTicketAttachment.builder()
                        .userId(updateTicketRequest.getRequestedBy())
                        .ticketId(ticket.getId())
                        .attachment_url(url).build();
                assigneeTicketAttachmentRepository.save(assigneeTicketAttachment);
            }
        }
    }

    /**
     *
     * @param ticket
     * @return
     */
    private org.upsmf.grievance.model.es.Ticket convertToESTicketObj(Ticket ticket) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtil.DEFAULT_DATE_FORMAT);
        ObjectMapper mapper = new ObjectMapper();
        // TODO get user details based on ID
        return org.upsmf.grievance.model.es.Ticket.builder()
                .ticketId(ticket.getId())
                .firstName(ticket.getFirstName())
                .lastName(ticket.getLastName())
                .phone(ticket.getPhone())
                .email(ticket.getEmail())
                .requesterType(ticket.getRequesterType())
                .assignedToId(ticket.getAssignedToId())
                .assignedToName("") // get user details based on ID
                .description(ticket.getDescription())
                .junk(ticket.isJunk())
                .createdDate(ticket.getCreatedDate().toLocalDateTime().format(dateTimeFormatter))
                .createdDateTS(ticket.getCreatedDate().getTime())
                .updatedDate(ticket.getUpdatedDate().toLocalDateTime().format(dateTimeFormatter))
                .updatedDateTS(ticket.getUpdatedDate().getTime())
                .lastUpdatedBy(ticket.getLastUpdatedBy())
                .escalated(ticket.isEscalated())
                .escalatedDate(ticket.getEscalatedDate()!=null?ticket.getEscalatedDate().toLocalDateTime().format(dateTimeFormatter):null)
                .escalatedDateTS(ticket.getEscalatedDate()!=null?ticket.getEscalatedDate().getTime():-1)
                .status(ticket.getStatus())
                .requestType(ticket.getRequestType())
                .priority(ticket.getPriority())
                .escalatedBy(ticket.getEscalatedBy())
                .escalatedTo(ticket.getEscalatedTo()).build();
                //.comments(ticket.getComments()!=null?ticket.getComments():Collections.EMPTY_LIST)
                //.raiserAttachmentURLs(ticket.getRaiserTicketAttachmentURLs()!=null?mapper.writeValueAsString(ticket.getRaiserTicketAttachmentURLs()):"")
                //.assigneeAttachmentURLs(ticket.getAssigneeTicketAttachment()!=null?mapper.writeValueAsString(ticket.getAssigneeTicketAttachment()):"").build();
    }

    /**
     *
     * @param id
     * @return
     */
    public Optional<org.upsmf.grievance.model.es.Ticket> getESTicketByID(long id) {
        return esTicketRepository.findOneByTicketId(id);
    }

    /**
     *
     * @param id
     * @return
     */
    public Optional<Ticket> getTicketDetailsByID(long id) {
        return ticketRepository.findById(id);
    }

    private void validateTicketRequest(TicketRequest ticketRequest) throws Exception{
        if(ticketRequest==null){
            throw new IllegalArgumentException("Ticket request cannot be null");
        }
        if(StringUtils.isBlank(ticketRequest.getFirstName())||StringUtils.isBlank(ticketRequest.getLastName())){
            throw new IllegalArgumentException("First name and last name are required");
        }

        if (StringUtils.isBlank(ticketRequest.getEmail())) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!isValidPhoneNumber(ticketRequest.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        if (ticketRequest.getUserType() == null) {
            throw new IllegalArgumentException("User type is required");
        }
        if (ticketRequest.getAttachmentURls() != null) {
            for (String attachmentUrl : ticketRequest.getAttachmentURls()) {
                if (StringUtils.isBlank(attachmentUrl)) {
                    throw new IllegalArgumentException("Invalid attachment URL");
                }
            }
        }
    }
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Validate phone number format using a regular expression
        String phonePattern = "\\d{10}";
        return Pattern.matches(phonePattern, phoneNumber);
    }

    private boolean isValidStatus(TicketStatus status) {
        return status == TicketStatus.OPEN ||  status==TicketStatus.CLOSED;
    }

    private boolean isValidPriority(TicketPriority priority) {
        return priority == TicketPriority.LOW || priority == TicketPriority.MEDIUM ||priority==TicketPriority.HIGH;
    }

    private void validateUpdateTicketRequest(UpdateTicketRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Update ticket request is null");
        }

        if (request.getStatus() == null || !isValidStatus(request.getStatus())) {
            throw new IllegalArgumentException("Invalid ticket status");
        }

        if (request.getPriority() == null || !isValidPriority(request.getPriority())) {
            throw new IllegalArgumentException("Invalid ticket priority");
        }


    }
}
