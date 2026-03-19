package com.example.userservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Report {
    
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long reportedUserId;

    @Column(nullable = false)
    private Long reporterUserId;

    public Report() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getReportedUserId() { return reportedUserId; }
    public void setReportedUserId(Long reportedUserId) { this.reportedUserId = reportedUserId; }
    public Long getReporterUserId() { return reporterUserId; }
    public void setReporterUserId(Long reporterUserId) { this.reporterUserId = reporterUserId; }
}


