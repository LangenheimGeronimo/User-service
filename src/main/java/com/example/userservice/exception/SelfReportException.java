package com.example.userservice.exception;

public class SelfReportException extends RuntimeException{
    public SelfReportException() {
        super("Un usuario no puede denunciarse a sí mismo.");
    }
}
//