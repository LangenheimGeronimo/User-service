package com.example.userservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportCreateDTO(
    @NotBlank(message = "El motivo no puede estar vacío")
    @Size(max = 500, message = "El motivo no puede superar los 500 caracteres")
    String reason,
    
    @NotNull(message = "El ID del denunciante es obligatorio")
    Long reporterUserId,
    
    @NotNull(message = "El ID del denunciado es obligatorio")
    Long reportedUserId
) {}
