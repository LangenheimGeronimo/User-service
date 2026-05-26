package com.example.userservice.exception;

public class SelfReportException extends RuntimeException{
    // Visible desde los tests
    public static final String DEFAULT_MESSAGE = "Un usuario no puede denunciarse a sí mismo";

    //Constructor por defecto
    public SelfReportException() {
        super(DEFAULT_MESSAGE);
    }

    // Constructor flexible 
    public SelfReportException(String message) {
        super(message);
    }
}
