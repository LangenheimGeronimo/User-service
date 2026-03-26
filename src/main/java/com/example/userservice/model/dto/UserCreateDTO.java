package com.example.userservice.model.dto;

import com.example.userservice.model.enums.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record UserCreateDTO(
    @Schema(description = "Nombre del usuario", example = "Geronimo", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Debe ingresar un nombre válido")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre solo puede contener letras")
    @Size(min = 2, max = 20, message = "El nombre de usuario no puede tener más de 20 caracteres")
    String firstName,

    @Schema(description = "Apellido del usuario", example = "Langenheim", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Debe ingresar un apellido válido")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El apellido solo puede contener letras")
    @Size(min = 2, max = 20, message = "El apellido de usuario no puede tener más de 20 caracteres")
    String lastName,

    @Schema(description = "Correo electrónico del usuario", example = "geronimo@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Debe ingresar un correo válido")
    @Email(message = "Debe ser una dirección de correo electrónico con formato correcto")
    String email,

    @Schema(description = "Contraseña del usuario", example = "mipassword1234", format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Debe ingresar una contraseña válida")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", 
             message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial")
    String password,

    @Schema(description = "Fecha de nacimiento del usuario", example = "19/04/2004", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha debe ser una fecha pasada")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate birthDate,

    @Schema(description = "Rol del usuario", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El rol es obligatorio")
    Role role
) {}