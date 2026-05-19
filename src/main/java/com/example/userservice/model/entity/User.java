package com.example.userservice.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.example.userservice.model.enums.Role;
import com.example.userservice.model.enums.State;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class User extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String firstName;
    @Column(nullable = false, length = 50)
    private String lastName;
    @Column(nullable = false)
    private LocalDate birthDate;
    @Column(nullable = false, unique = true, length = 100) 
    private String email;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private State state; 
    @Column(nullable = false)
    private String password;
    @Transient 
    @Builder.Default 
    private List<Long> orderIds = new ArrayList<>();
    @Column(name = "ban_until")
    private LocalDateTime banUntil;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id != null && id.equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}