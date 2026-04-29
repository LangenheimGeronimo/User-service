package com.example.userservice.service.impl;

import com.example.userservice.model.dto.AuthResponse;
import com.example.userservice.model.dto.LoginDTO;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtUtils;
import com.example.userservice.security.UserPrincipal;
import com.example.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    // REPASAR SOOLO PARA VER SI SE ENTENDIO
    @Override
    public AuthResponse login(LoginDTO loginDto) {
        logger.info("Iniciando proceso de login para el usuario: {}", loginDto.email());
        // 1. Autenticación
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.email(),
                        loginDto.password()));

        // 2. Búsqueda del usuario
        var user = userRepository.findByEmail(loginDto.email())
                .orElseThrow(() -> {
                    logger.error("ERROR LÓGICO: El AuthenticationManager validó al usuario {}, pero no existe en la DB", loginDto.email());
                    return new UsernameNotFoundException("Credenciales inválidas o cuenta inexistente");
                });

        // 3. Generación del token (Envolvemos el 'user' en 'UserPrincipal')
        String token = jwtUtils.generateToken(new UserPrincipal(user));

        return new AuthResponse(token);

    }
}