package com.example.userservice.model.dto;

import com.example.userservice.model.enums.Role; 
import com.example.userservice.validator.ValidAge;
import com.example.userservice.validator.ValidEmail;
import com.example.userservice.validator.ValidName;
import com.example.userservice.validator.ValidPassword;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UserCreateDTO(
    @Schema(description = "Nombre del usuario", example = "Geronimo", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre no puede estar vacío")
    @ValidName(message = "El nombre debe tener entre 2 y 20 letras")
    String firstName,

    @Schema(description = "Apellido del usuario", example = "Langenheim", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El apellido no puede estar vacío")
    @ValidName(message = "El apellido debe tener entre 2 y 20 letras")
    String lastName,

    @Schema(description = "Correo electrónico del usuario", example = "geronimo@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @ValidEmail
    String email,

    @Schema(description = "Contraseña del usuario", example = "mipassword1234", format = "password", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "La contraseña no puede estar vacía")
    @ValidPassword        
    String password,

    @Schema(description = "Fecha de nacimiento del usuario", example = "19/04/2004", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @ValidAge
    LocalDate birthDate,

    @Schema(description = "Rol del usuario", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "El rol es obligatorio")
    Role role
) {}