package com.example.userservice.exception; 

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // MANEJO DE VALIDACIONES 
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    
    ex.getBindingResult().getFieldErrors().forEach((error) -> {
        errors.put(error.getField(), error.getDefaultMessage());
    });
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("Error de validación", LocalDateTime.now(), errors));
    }

    // MANEJO DE SISTEMA GENERAL
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        logger.error("Error no controlado detectado: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ErrorResponse("Ocurrió un error inesperado, intente más tarde", LocalDateTime.now()));
    }

    //MANEJOS DE PERSONALIZADOS:  

    // MANEJO DE EXISTENCIA DE EMAIL (Negocio)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailDuplicate(EmailAlreadyExistsException ex) {
        logger.warn("Recurso ya existente: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT) 
                .body(new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }

    // MANEJO DE USUARIO ELIMINADO ANTERIORMENTE (Negocio)
    @ExceptionHandler(UserIsAlreadyDeletedException.class)
    public ResponseEntity<ErrorResponse> handleUserEliminated(UserIsAlreadyDeletedException ex){
        logger.warn("Recurso ya eliminado: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.GONE)
                .body(new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }

    // MANEJO DE USUARIO NO ENCONTRADO (NEGOCIO)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(UserNotFoundException ex) {
        logger.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }

    //MANEJO DE REPORTE NO EJECUTADO (NEGOCIO)
    @ExceptionHandler(AlreadyReportedException.class)
    public ResponseEntity<ErrorResponse> handleBadReport(AlreadyReportedException ex){
        logger.warn("Conflicto en reporte: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }

    // MANEJO DE CREDENCIALES ERRONEAS (SEGURIDAD)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
    logger.error("Error de autenticación: {}", ex.getMessage());
    ErrorResponse error = new ErrorResponse(
        ex.getMessage(),
        LocalDateTime.now()
    );
    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED); // El 401 es el correcto para login fallido
    }

    // MANEJO DE ACCESO DENEGADO - RECURSO PROHIBIDO (SEGURIDAD)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        logger.warn("Acceso denegado: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(
            "No tienes permisos para realizar esta operación o acceder a este recurso.", 
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN); // 403 Forbidden
    }
}

