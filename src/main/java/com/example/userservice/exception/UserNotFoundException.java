package com.example.userservice.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User not exists");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
