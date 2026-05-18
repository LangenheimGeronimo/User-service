package com.example.userservice.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Usuario no encontrado.");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
