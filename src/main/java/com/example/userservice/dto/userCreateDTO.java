package com.example.userservice.dto;

import java.time.LocalDate;
import com.example.userservice.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public class UserCreateDTO {

    @NotBlank (message = "Debe ingresar un nombre valido")
    @Size(min = 5, max = 20, message = "El nombre de usuario no puede tener más de 20 caracteres")
    private String firstName;
    @NotBlank (message = "Debe ingresar un apellido valido")
    @Size(min = 5, max = 20, message = "El apellido de usuario no puede tener más de 20 caracteres")
    private String lastName;
    @NotBlank (message = "Debe ingresar un nombre valido")
    @Email
    private String email;
    @NotBlank (message = "Debe ingresar una contraseña valida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
    @NotNull
    @Past
    private LocalDate birthDate;
    @NotNull
    private Role role; // o asignarlo fijo en el service

    public UserCreateDTO() {
    }

    public UserCreateDTO(String firstName, String lastName, String email, String password, LocalDate birthDate,
			Role role) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.birthDate = birthDate;
		this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
    
    
    
}
