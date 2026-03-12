package com.example.userservice.dto;

import java.util.List;

public record UserResponseDTO(
    Long id,
    String firstName,
    String lastName,
    String email,
    List<Long> orderIds
) {}