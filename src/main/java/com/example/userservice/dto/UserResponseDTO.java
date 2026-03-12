package com.example.userservice.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponseDTO(
    @Schema(description = "ID único del usuario", example = "1")
    Long id,
    @Schema(description = "Nombre del usuario", example = "Geronimo")
    String firstName,
    @Schema(description = "Apellido del usuario", example = "Langenheim")
    String lastName,
    @Schema(description = "Email del usuario", example = "geronimo@email.com")
    String email,
    @Schema(description = "Lista de IDs de pedidos asociados al usuario", example = "[101, 102]")
    List<Long> orderIds
) {}