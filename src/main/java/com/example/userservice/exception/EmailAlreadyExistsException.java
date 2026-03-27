package com.example.userservice.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException() {
        super("Email already in use");
    }

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
