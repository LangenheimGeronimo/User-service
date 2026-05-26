package com.example.userservice.exception;

public class InvalidStateException extends IllegalArgumentException {
    // Visible desde los tests
    public static final String DEFAULT_MESSAGE = "El estado enviado no es válido para esta operación";

    //Constructor por defecto
    public InvalidStateException() {
        super(DEFAULT_MESSAGE);
    }

    // Constructor flexible 
    public InvalidStateException(String message) {
        super(message);
    }

}