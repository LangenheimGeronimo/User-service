package com.example.userservice.dto;

import com.example.userservice.model.Role;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record UserCreateDTO(
    @NotBlank(message = "Debe ingresar un nombre válido")
    @Size(min = 5, max = 20, message = "El nombre de usuario no puede tener más de 20 caracteres")
    String firstName,

    @NotBlank(message = "Debe ingresar un apellido válido")
    @Size(min = 5, max = 20, message = "El apellido de usuario no puede tener más de 20 caracteres")
    String lastName,

    @NotBlank(message = "Debe ingresar un correo válido")
    @Email(message = "Debe ser una dirección de correo electrónico con formato correcto")
    String email,

    @NotBlank(message = "Debe ingresar una contraseña válida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    String password,

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha debe ser una fecha pasada")
    LocalDate birthDate,

    @NotNull(message = "El rol es obligatorio")
    Role role
) {}