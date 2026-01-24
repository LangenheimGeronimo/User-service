package com.example.userservice.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class User {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String firstName;
	private String lastName;
	
	private LocalDate birthDate;
	
	@Transient //este campo no se guarda en la base de datos
	private List<Long> orderIds;
	
	@Column(nullable = false, unique = true) 
	private String email;
	
	private boolean active;
	
	@Enumerated(EnumType.STRING)
	private Role role; 
	
	@Enumerated(EnumType.STRING)
	private State state; 
	
	private String password;
	
	public User() {
		super();
		this.orderIds = new ArrayList<>();
	}

	public User(Long id, String firstName, String lastName, LocalDate birthDate,
            String email, boolean active, Role role, State state, String password) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.birthDate = birthDate;
    this.email = email;
    this.active = active;
    this.role = role;
    this.state = state;
    this.password = password;
    this.orderIds = new ArrayList<>();
}

	// GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public List<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

	// FINALIZAR CONTROLLER
	// HACER RESPASO Y ENTENDIMIENTO COMPLETO
	// COMPLETAR CASOS DE USO

