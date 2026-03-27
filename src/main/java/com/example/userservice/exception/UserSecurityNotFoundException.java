package com.example.userservice.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserSecurityNotFoundException extends UsernameNotFoundException {
    public UserSecurityNotFoundException() {
        super("Security: User not found in system");
    }
}