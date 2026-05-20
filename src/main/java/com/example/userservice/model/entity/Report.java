package com.example.userservice.model.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "reports", indexes = {
    @Index(name = "idx_report_reported", columnList = "reported_user_id"),
    @Index(name = "idx_report_unique_pair", columnList = "reporter_user_id, reported_user_id") 
})
@Getter 
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder 
@SQLDelete(sql = "UPDATE reports SET active = false WHERE id = ?") 
@SQLRestriction("active = true") 
public class Report extends Auditable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500) 
    private String reason;

    @Column(name = "reported_user_id", nullable = false)
    private Long reportedUserId;

    @Column(name = "reporter_user_id", nullable = false)
    private Long reporterUserId;

    @Builder.Default
    private boolean active = true;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Report report)) return false;
        return id != null && id.equals(report.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

