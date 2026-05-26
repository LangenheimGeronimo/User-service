package com.example.userservice.exception;

public class UserNotFoundException extends RuntimeException {

    // Visible desde los tests
    public static final String DEFAULT_MESSAGE = "Usuario no encontrado";

    //Constructor por defecto
    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    // Constructor flexible 
    public UserNotFoundException(String message) {
        super(message);
    }
}
