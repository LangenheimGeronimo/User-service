package com.example.userservice.model.dto;

import java.time.LocalDate;
import java.util.List;
import com.example.userservice.model.enums.Role;
import com.example.userservice.model.enums.State;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @Schema(description = "Fecha de nacimiento", example = "19/04/2004")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate birthDate,

    @Schema(description = "Rol asignado", example = "USER")
    Role role,

    @Schema(description = "Estado actual de la cuenta", example = "ACTIVE")
    State state,
    @Schema(description = "Lista de IDs de pedidos asociados al usuario", example = "[101, 102]")
    List<Long> orderIds
) {
    public UserResponseDTO {
        orderIds = (orderIds == null) ? List.of() : List.copyOf(orderIds);
    }
}