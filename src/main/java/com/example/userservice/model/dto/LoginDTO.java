package com.example.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record LoginDTO(
        @Schema(description = "Correo electrónico del usuario", example = "geronimo@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Debe ingresar un correo válido")
        @Email(message = "Debe ser una dirección de correo electrónico con formato correcto")
        String email,
        @Schema(description = "Contraseña del usuario", example = "mipassword1234", format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Debe ingresar una contraseña válida")
        String password
    ) {}

