package com.example.userservice.model.dto;

import com.example.userservice.validator.ValidAge;
import com.example.userservice.validator.ValidName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UserUpdateDTO(
    @Schema(description = "Nombre del usuario", example = "Geronimo", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El nombre no puede estar vacío")
    @ValidName(message = "El nombre debe tener entre 2 y 20 letras")
    String firstName,

    @Schema(description = "Apellido del usuario", example = "Langenheim", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "El apellido no puede estar vacío")
    @ValidName(message = "El apellido debe tener entre 2 y 20 letras")
    String lastName,

    @Schema(description = "Fecha de nacimiento del usuario", example = "19/04/2004", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @ValidAge
    LocalDate birthDate
) {}