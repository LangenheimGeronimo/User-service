package com.example.userservice.controller;

import com.example.userservice.model.dto.AuthResponse;
import com.example.userservice.model.dto.LoginDTO;
import com.example.userservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints para registro y login")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Iniciar sesión para obtener el token JWT")
    @PostMapping("/login") 
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDTO loginDto) {
        log.info("Recibida petición de login para el usuario: {}", loginDto.email());
        
        AuthResponse response = authService.login(loginDto);
        
        log.info("Login procesado exitosamente para: {}", loginDto.email());
        return ResponseEntity.ok(response); 
    }
}