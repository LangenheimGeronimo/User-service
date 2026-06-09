package com.example.userservice.controller;

import com.example.userservice.config.SecurityConfig;
import com.example.userservice.exception.GlobalExceptionHandler;
import com.example.userservice.model.dto.RegisterDTO;
import com.example.userservice.model.dto.UserResponseDTO;
import com.example.userservice.model.enums.Role;
import com.example.userservice.model.enums.State;
import com.example.userservice.security.JwtAuthenticationFilter;
import com.example.userservice.security.JwtUtils;
import com.example.userservice.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.List;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@AutoConfigureMockMvc
@ActiveProfiles("test")
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean(name = "userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() throws Exception {
        // Mockeamos el FilterChain para que deje pasar las peticiones simuladas de MockMvc sin trabarse en el filtro JWT
        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
@DisplayName("Debe retornar 201 Created al registrar un usuario con datos válidos")
@SuppressWarnings("null")
void register_ShouldReturn201_WhenDataIsValid() throws Exception {
    String validJson = """
        {
            "firstName": "Geronimo",
            "lastName": "Langenheim",
            "email": "geronimo@email.com",
            "password": "SecurePass123!",
            "birthDate": "19/04/2004"
        }
        """;
    
    UserResponseDTO expectedResponse = new UserResponseDTO(
        1L, "Geronimo", "Langenheim", "geronimo@email.com", LocalDate.of(2004, 4, 19), Role.USER, State.ACTIVE, List.of()
    );

    when(authService.register(any(RegisterDTO.class))).thenReturn(expectedResponse);

    mockMvc.perform(post("/api/v1/auth/register")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(validJson)) 
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("geronimo@email.com"))
            .andExpect(jsonPath("$.firstName").value("Geronimo"));
}
    
    @Test
    @DisplayName("Debe retornar 400 Bad Request cuando el DTO de registro tiene datos inválidos")
    @SuppressWarnings("null")
    void register_ShouldReturn400_WhenDataIsInvalid() throws Exception {
        RegisterDTO invalidDto = new RegisterDTO(
            "Geronimo", "Langenheim", "email_malo_sin_arroba", "mipassword1234", LocalDate.of(2004, 4, 19)
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()); // Se frena antes de tocar el servicio
    }
}
