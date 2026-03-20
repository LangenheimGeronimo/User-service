package com.example.userservice.exceptions;

public class AlreadyReportedException extends RuntimeException{
    public AlreadyReportedException(String message) {
        super(message);
    }
}
