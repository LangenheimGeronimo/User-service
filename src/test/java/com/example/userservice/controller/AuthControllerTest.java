package com.example.userservice.controller;

import com.example.userservice.model.dto.RegisterDTO;
import com.example.userservice.model.dto.UserResponseDTO;
import com.example.userservice.model.enums.Role;
import com.example.userservice.model.enums.State;
import com.example.userservice.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.List;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc; 

    @MockitoBean
    private AuthService authService; 

    @Autowired
    private ObjectMapper objectMapper; 

    @Test
    @DisplayName("Debe retornar 201 Created al registrar un usuario con datos válidos")
    @SuppressWarnings("null")
    void register_ShouldReturn201_WhenDataIsValid() throws Exception {
        RegisterDTO registerDto = new RegisterDTO(
            "Geronimo", "Langenheim", "geronimo@email.com", "mipassword1234", LocalDate.of(2004, 4, 19)
        );
        
        UserResponseDTO expectedResponse = new UserResponseDTO(
            1L, 
            "Geronimo", 
            "Langenheim", 
            "geronimo@email.com", 
            LocalDate.of(2004, 4, 19), 
            Role.USER, 
            State.ACTIVE, 
            List.of()
        );

        when(authService.register(any(RegisterDTO.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto))) 
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("geronimo@email.com"))
                .andExpect(jsonPath("$.firstName").value("Geronimo"));
    }

    @Test
    @DisplayName("Debe retornar 400 Bad Request cuando el DTO de registro tiene datos inválidos")
    @SuppressWarnings("null")
    void register_ShouldReturn400_WhenDataIsInvalid() throws Exception {
        // Arrange - Mandamos un email inválido para que salte la validación
        RegisterDTO invalidDto = new RegisterDTO(
            "Geronimo", "Langenheim", "email_malo_sin_arroba", "mipassword1234", LocalDate.of(2004, 4, 19)
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()); // Se frena antes de tocar el servicio
    }
}
