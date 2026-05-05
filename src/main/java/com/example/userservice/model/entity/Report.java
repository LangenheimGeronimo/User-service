package com.example.userservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reports")
@Data 
@NoArgsConstructor
@AllArgsConstructor
@Builder 
public class Report extends Auditable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reason", nullable = false, length = 500) // Limitamos longitud
    private String reason;


    @Column(name = "reported_user_id", nullable = false)
    private Long reportedUserId;

    @Column(name = "reporter_user_id", nullable = false)
    private Long reporterUserId;

}