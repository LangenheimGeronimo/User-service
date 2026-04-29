package com.example.userservice.controller;

import com.example.userservice.model.dto.AuthResponse;
import com.example.userservice.model.dto.LoginDTO;
import com.example.userservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; // Necesitarás la dependencia de validation
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints para registro y login")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @Operation(summary = "Iniciar sesión para obtener el token JWT")
    @PostMapping("/login") 
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDTO loginDto) {
        logger.info("Recibida petición de login para el usuario: {}", loginDto.email());
        
        AuthResponse response = authService.login(loginDto);
        
        logger.info("Login procesado exitosamente para: {}", loginDto.email());
        return ResponseEntity.ok(response); 
    }
}