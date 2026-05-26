package com.example.userservice.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserSecurityNotFoundException extends UsernameNotFoundException {

    // Visible desde los tests
    public static final String DEFAULT_MESSAGE = "Security: User not found in system";

    //Constructor por defecto
    public UserSecurityNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    // Constructor flexible 
    public UserSecurityNotFoundException(String message) {
        super(message);
    }
}