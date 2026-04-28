package com.example.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
    @Schema(description = "Token JWT de acceso", example = "eyJhbGciOiJIUzI1...")
    String token
) {}