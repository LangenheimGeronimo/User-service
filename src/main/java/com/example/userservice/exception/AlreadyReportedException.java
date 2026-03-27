package com.example.userservice.exception;

public class AlreadyReportedException extends RuntimeException{
    public AlreadyReportedException() {
        super("El usuario ya ha realizado una denuncia previa contra este perfil.");
    }
}
