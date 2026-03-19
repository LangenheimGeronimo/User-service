package com.example.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportCreateDTO(
    @NotBlank(message = "El motivo no puede estar vacío")
    String reason,
    
    @NotNull(message = "El ID del denunciante es obligatorio")
    Long reporterUserId,
    
    @NotNull(message = "El ID del denunciado es obligatorio")
    Long reportedUserId
) {}
