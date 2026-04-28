package com.example.userservice.service.impl; 

import com.example.userservice.model.dto.AuthResponse;
import com.example.userservice.model.dto.LoginDTO;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.security.JwtUtils;
import com.example.userservice.security.UserPrincipal;
import com.example.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    //REPASAR SOOLO PARA VER SI SE ENTENDIO
    @Override
    public AuthResponse login(LoginDTO loginDto) {
        // 1. Autenticación 
        authenticationManager.authenticate( 
            new UsernamePasswordAuthenticationToken( 
                loginDto.email(), 
                loginDto.password()
            )
        );

        // 2. Búsqueda del usuario
        var user = userRepository.findByEmail(loginDto.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Generación del token (Envolvemos el 'user' en 'UserPrincipal')
        String token = jwtUtils.generateToken(new UserPrincipal(user));

        return new AuthResponse(token);

    }
}