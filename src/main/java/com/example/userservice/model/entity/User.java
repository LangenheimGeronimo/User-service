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
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class User extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    
    @Column(nullable = false, unique = true) 
    private String email;
    
    @Enumerated(EnumType.STRING)
    private Role role; 
    
    @Enumerated(EnumType.STRING)
    private State state; 
    
    private String password;

    @Transient 
    @Builder.Default 
    private List<Long> orderIds = new ArrayList<>();

    @Column(name = "ban_until")
    private LocalDateTime banUntil;
}