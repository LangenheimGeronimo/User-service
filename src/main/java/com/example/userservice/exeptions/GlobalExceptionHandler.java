package com.example.userservice.exeptions; 

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.MethodArgumentNotValidException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // MANEJO DE NEGOCIO (Lo que tú lanzas)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }

    // MANEJO DE VALIDACIONES (Lo que Spring lanza automáticamente)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    
    ex.getBindingResult().getFieldErrors().forEach((error) -> {
        errors.put(error.getField(), error.getDefaultMessage());
    });
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ErrorResponse("Error de validación", LocalDateTime.now(), errors));
    }

    // MANEJO DE SISTEMA (El "salvavidas" general)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        // Aquí NO devuelves el ex.getMessage() porque podrías revelar secretos del sistema
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ErrorResponse("Ocurrió un error inesperado, intente más tarde", LocalDateTime.now()));
    }

    //PERSONALIZADAS:

    // MANEJO DE EXISTENCIA DE EMAIL (Negocio)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailDuplicate(EmailAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // Código 409 es el estándar para duplicados
                .body(new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }

    // MANEJO DE USUARIO ELIMINADO ANTERIORMENTE (Negocio)
    @ExceptionHandler(UserIsAlreadyDeletedException.class)
    public ResponseEntity<ErrorResponse> handleUserEliminated(UserIsAlreadyDeletedException ex){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage(), LocalDateTime.now()));
    }
}