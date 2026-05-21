package com.example.userservice.model.entity;

import com.example.userservice.model.enums.State;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_status_history", indexes = {
    @Index(name = "idx_status_history_user", columnList = "user_id") 
})
@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatusHistory extends Auditable { 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_state") 
    private State previousState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false) 
    private State newState;

    @Column(length = 500) 
    private String reason;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserStatusHistory that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}