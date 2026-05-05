package com.example.userservice.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException() {
        super("Resource not found");
    }

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
