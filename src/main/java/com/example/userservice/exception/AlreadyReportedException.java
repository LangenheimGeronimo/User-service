package com.example.userservice.exception;

public class AlreadyReportedException extends RuntimeException{
    // Visible desde los tests.
    public static final String DEFAULT_MESSAGE = "El usuario ya ha realizado una denuncia previa contra este perfil.";

    //Constructor por defecto
    public AlreadyReportedException() {
        super(DEFAULT_MESSAGE);
    }

    // Constructor flexible 
    public AlreadyReportedException(String message) {
        super(message);
    }
}


