package com.example.userservice.exception;

public class UserIsAlreadyDeletedException extends RuntimeException {

    // Visible desde los tests
    public static final String DEFAULT_MESSAGE = "La cuenta se encuentra inhabilitada";

    //Constructor por defecto
    public UserIsAlreadyDeletedException() {
        super(DEFAULT_MESSAGE);
    }

    // Constructor flexible 
    public UserIsAlreadyDeletedException(String message) {
        super(message);
    }

}
