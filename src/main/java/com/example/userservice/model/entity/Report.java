package com.example.userservice.model.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
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
@SQLDelete(sql = "UPDATE reports SET active = false WHERE id = ?") 
@SQLRestriction("active = true") 
public class Report extends Auditable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reason", nullable = false, length = 500) 
    private String reason;


    @Column(name = "reported_user_id", nullable = false)
    private Long reportedUserId;

    @Column(name = "reporter_user_id", nullable = false)
    private Long reporterUserId;

    @Builder.Default
    private boolean active = true;
}

