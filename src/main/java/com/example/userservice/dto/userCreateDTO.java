package com.example.userservice.dto;

import com.example.userservice.model.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record UserCreateDTO(
    @Schema(description = "Nombre del usuario", example = "Geronimo", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Debe ingresar un nombre válido")
    @Size(min = 5, max = 20, message = "El nombre de usuario no puede tener más de 20 caracteres")
    String firstName,

    @Schema(description = "Apellido del usuario", example = "Langenheim")
    @NotBlank(message = "Debe ingresar un apellido válido")
    @Size(min = 5, max = 20, message = "El apellido de usuario no puede tener más de 20 caracteres")
    String lastName,

    @Schema(description = "Correo electrónico del usuario", example = "geronimo@email.com")
    @NotBlank(message = "Debe ingresar un correo válido")
    @Email(message = "Debe ser una dirección de correo electrónico con formato correcto")
    String email,

    @Schema(description = "Contraseña del usuario", example = "mipassword1234")
    @NotBlank(message = "Debe ingresar una contraseña válida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    String password,

    @Schema(description = "Fecha de nacimiento del usuario", example = "19/04/2004")
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha debe ser una fecha pasada")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate birthDate,

    @Schema(description = "Rol del usuario", example = "ADMIN")
    @NotNull(message = "El rol es obligatorio")
    Role role
) {}