package com.example.userservice.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException() {
        super("El email ya se encuentra registrado en el sistema.");
    }

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
