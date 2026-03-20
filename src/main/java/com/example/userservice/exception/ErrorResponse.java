package com.example.userservice.exception;

import java.time.LocalDateTime;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String message, LocalDateTime timestamp, Map<String, String> errors) {

    public ErrorResponse(String message, LocalDateTime timestamp) {
        this(message, timestamp, null);
    }
    
}
