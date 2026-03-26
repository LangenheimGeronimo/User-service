package com.example.userservice.model.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reports")
@Data // Genera getters, setters, toString, equals y hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder // Útil para crear reportes en tests o servicios de forma limpia
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reason", nullable = false, length = 500) // Limitamos longitud
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "reported_user_id", nullable = false)
    private Long reportedUserId;

    @Column(name = "reporter_user_id", nullable = false)
    private Long reporterUserId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}