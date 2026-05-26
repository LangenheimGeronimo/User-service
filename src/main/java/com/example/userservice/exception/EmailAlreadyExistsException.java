package com.example.userservice.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    // Visible desde los tests
    public static final String DEFAULT_MESSAGE = "El email ya se encuentra registrado.";

    //Constructor por defecto
    public EmailAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }

    // Constructor flexible 
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
