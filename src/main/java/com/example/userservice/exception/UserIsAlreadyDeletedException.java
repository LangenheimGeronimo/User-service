package com.example.userservice.exception;

public class UserIsAlreadyDeletedException extends RuntimeException {

    public UserIsAlreadyDeletedException(){
        super("La cuenta se encuentra inhabilitada.");
    }

    public UserIsAlreadyDeletedException(String message){
        super(message);
    }

}
