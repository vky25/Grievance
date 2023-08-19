package org.upsmf.grievance.model;

import lombok.*;

import javax.persistence.*;
@Entity
@Table(name = "department")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String departmentName;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
