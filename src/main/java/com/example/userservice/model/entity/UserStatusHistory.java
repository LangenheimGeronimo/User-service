package com.example.userservice.model.entity;

import com.example.userservice.model.enums.State;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_status_history")
@Data
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
    @Column(name = "new_state", nullable = false)
    private State newState;

    @Column(name = "reason", length = 500)
    private String reason;
}