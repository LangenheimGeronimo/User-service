package com.example.userservice.exception;

public class UserIsAlreadyDeletedException extends RuntimeException {
    public UserIsAlreadyDeletedException(String message){
        super(message);
    }
}
