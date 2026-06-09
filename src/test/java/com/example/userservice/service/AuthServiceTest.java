package com.example.userservice.service;

import com.example.userservice.exception.EmailAlreadyExistsException;
import com.example.userservice.model.dto.RegisterDTO;
import com.example.userservice.model.dto.UserCreateDTO;
import com.example.userservice.model.dto.UserResponseDTO;
import com.example.userservice.model.enums.Role;
import com.example.userservice.model.enums.State;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository; 

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Debe registrar un usuario exitosamente forzando el Role.USER")
    void register_ShouldCreateUserWithUserRole() {
        RegisterDTO registerDto = new RegisterDTO(
            "Geronimo", "Langenheim", "geronimo@email.com", "mipassword1234", LocalDate.of(2004, 4, 19)
        );
        
        UserResponseDTO expectedResponse = new UserResponseDTO(
            1L, "Geronimo", "Langenheim", "geronimo@email.com", LocalDate.of(2004, 4, 19), Role.USER, State.ACTIVE, List.of()
        );

        when(userService.createUser(any(UserCreateDTO.class))).thenReturn(expectedResponse);

        UserResponseDTO result = authService.register(registerDto);

        assertNotNull(result);
        assertEquals("geronimo@email.com", result.email());
        
        ArgumentCaptor<UserCreateDTO> captor = ArgumentCaptor.forClass(UserCreateDTO.class);
        verify(userService).createUser(captor.capture());
        
        UserCreateDTO capturedCreateDto = captor.getValue();
        assertEquals(Role.USER, capturedCreateDto.role(), "El rol debe ser estrictamente USER");
    }

    @Test
    @DisplayName("Debe lanzar una excepción si el email ya se encuentra registrado")
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        RegisterDTO registerDto = new RegisterDTO(
            "Geronimo", "Langenheim", "duplicado@email.com", "mipassword1234", LocalDate.of(2004, 4, 19)
        );

        when(userService.createUser(any(UserCreateDTO.class)))
            .thenThrow(new EmailAlreadyExistsException()); 

        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.register(registerDto);
        });
    }
}