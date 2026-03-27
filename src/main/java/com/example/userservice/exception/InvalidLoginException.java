package com.example.userservice.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class InvalidLoginException extends BadCredentialsException {
    public InvalidLoginException() {
        super("Usuario o contraseña incorrectos");
    }
}
