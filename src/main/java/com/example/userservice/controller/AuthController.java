package com.example.userservice.controller;

import com.example.userservice.model.dto.AuthResponse;
import com.example.userservice.model.dto.LoginDTO;
import com.example.userservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    private final AuthService authService;

    @Operation(summary = "Iniciar sesión para obtener el token JWT")
    @PostMapping("/login") 
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDTO loginDto) {
        return ResponseEntity.ok(authService.login(loginDto)); 
    }
}


