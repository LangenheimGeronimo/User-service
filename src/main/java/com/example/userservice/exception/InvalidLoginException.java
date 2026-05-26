package com.example.userservice.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class InvalidLoginException extends BadCredentialsException {
    // Visible desde los tests
    public static final String DEFAULT_MESSAGE = "Usuario o contraseña incorrectos.";

    //Constructor por defecto
    public InvalidLoginException() {
        super(DEFAULT_MESSAGE);
    }

    // Constructor flexible 
    public InvalidLoginException(String message) {
        super(message);
    }
}
