package com.example.userservice.service.impl;

import com.example.userservice.model.dto.AuthResponse;
import com.example.userservice.model.dto.LoginDTO;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtUtils;
import com.example.userservice.security.UserPrincipal;
import com.example.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Override
    public AuthResponse login(LoginDTO loginDto) {
        log.info("Iniciando proceso de login para el usuario: {}", loginDto.email());
        // Autenticación
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.email(),
                        loginDto.password())
        );

        var user = userRepository.findByEmail(loginDto.email())
                .orElseThrow(() -> {
                    log.error("ERROR LÓGICO: El AuthenticationManager validó al usuario {}, pero no existe en la DB", loginDto.email());
                    return new UsernameNotFoundException("Credenciales inválidas o cuenta inexistente");
                });

        String token = jwtUtils.generateToken(new UserPrincipal(user));

        return new AuthResponse(token);

    }
}