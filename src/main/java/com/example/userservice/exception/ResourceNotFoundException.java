package com.example.userservice.exception;

public class ResourceNotFoundException extends RuntimeException {
    // Visible desde los tests
    public static final String DEFAULT_MESSAGE = "El recurso solicitado no fue encontrado";

    //Constructor por defecto
    public ResourceNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    // Constructor flexible 
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
