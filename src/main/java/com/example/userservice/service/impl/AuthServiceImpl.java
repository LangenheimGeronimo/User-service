package com.example.userservice.service.impl;

import com.example.userservice.model.dto.AuthResponse;
import com.example.userservice.model.dto.LoginDTO;
import com.example.userservice.model.dto.RegisterDTO;
import com.example.userservice.model.dto.UserCreateDTO;
import com.example.userservice.model.dto.UserResponseDTO;
import com.example.userservice.model.enums.Role;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtUtils;
import com.example.userservice.security.UserPrincipal;
import com.example.userservice.service.AuthService;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Override
    public AuthResponse login(LoginDTO loginDto) {
        log.info("Iniciando proceso de login para el usuario: {}", loginDto.email());
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

    @Override
    @Transactional 
    public UserResponseDTO register(RegisterDTO registerDto) {
        UserCreateDTO createDto = new UserCreateDTO(
            registerDto.firstName(),
            registerDto.lastName(),
            registerDto.email(),
            registerDto.password(),
            registerDto.birthDate(),
            Role.USER 
        );

        return userService.createUser(createDto);
    }
}