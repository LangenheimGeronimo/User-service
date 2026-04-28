package com.example.userservice.security;

import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor 
public class UserDetailsServiceImpl implements UserDetailsService { 

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository.findByEmail(email).map(UserPrincipal::new) 
            .orElseThrow(() -> {
                    logger.error("Usuario no encontrado con el email: {}", email);
                    return new UsernameNotFoundException("No se encontró el usuario con las credenciales proporcionadas");
            });
    }
}



