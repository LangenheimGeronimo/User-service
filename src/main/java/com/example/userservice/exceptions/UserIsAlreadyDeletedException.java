package com.example.userservice.exceptions;

public class UserIsAlreadyDeletedException extends RuntimeException {
    public UserIsAlreadyDeletedException(String message){
        super(message);
    }
}
