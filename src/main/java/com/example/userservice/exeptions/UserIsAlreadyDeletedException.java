package com.example.userservice.exeptions;

public class UserIsAlreadyDeletedException extends RuntimeException {
    public UserIsAlreadyDeletedException(String message){
        super(message);
    }
}
