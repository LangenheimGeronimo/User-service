package com.example.userservice.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("El recurso solicitado no fue encontrado");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
