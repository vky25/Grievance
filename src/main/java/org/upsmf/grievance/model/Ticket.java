package org.upsmf.grievance.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.upsmf.grievance.enums.RequesterType;
import org.upsmf.grievance.enums.TicketPriority;
import org.upsmf.grievance.enums.TicketStatus;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "ticket")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "requester_first_name")
    private String firstName;

    @Column(name = "requester_last_name")
    private String lastName;

    @Column(name = "requester_phone")
    private String phone;

    @Column(name = "requester_email")
    private String email;

    @Column(name = "requester_type")
    private RequesterType requesterType;

    @Column(name = "assigned_to_id")
    private String assignedToId;

    @Column(name = "description")
    private String description;

    @Column(name = "is_junk")
    private boolean junk = false;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    @Column(name = "created_date")
    private Timestamp createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    @Column(name = "updated_date")
    private Timestamp updatedDate;

    @Column(name = "last_updated_by")
    private String lastUpdatedBy;

    @Column(name = "is_escalated")
    private boolean escalated;

    @Column(name = "is_escalated_to_admin")
    private boolean escalatedToAdmin;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    @Column(name = "escalated_date")
    private Timestamp escalatedDate;

    @Column(name = "escalated_to")
    private String escalatedTo;

    @Column(name = "status")
    private TicketStatus status = TicketStatus.OPEN;

    @Column(name = "request_type")
    private String requestType;

    @Column(name = "priority")
    private TicketPriority priority = TicketPriority.LOW;

    // if the ticket is escalated by system, value will be -1 else superAdmin ID
    @Column(name = "escalated_by")
    private String escalatedBy = "-1";

    @OneToMany(targetEntity = Comments.class, mappedBy = "ticketId", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Comments> comments;

    @OneToMany(targetEntity = RaiserTicketAttachment.class, mappedBy = "ticketId", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<RaiserTicketAttachment> raiserTicketAttachmentURLs;

    @OneToMany(targetEntity = AssigneeTicketAttachment.class, mappedBy = "ticketId", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<AssigneeTicketAttachment> assigneeTicketAttachment;
}
