package com.example.userservice.model.dto;

import java.time.LocalDateTime;

public record ReportResponseDTO(
    Long id,
    String reason,
    Long reportedUserId,
    Long reporterUserId,
    boolean active,
    LocalDateTime createdAt
) {}