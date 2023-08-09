package org.upsmf.grievance.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name ="ticket_comments")
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comments {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "comment", nullable = false)
    private String comment;
}
