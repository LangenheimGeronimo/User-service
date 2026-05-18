package com.example.userservice.exception;

public class InvalidStateException extends IllegalArgumentException {
    public InvalidStateException() {
        super("El estado enviado no es válido para esta operación.");
    }
}