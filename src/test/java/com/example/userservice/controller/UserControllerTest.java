package com.example.userservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;

import com.example.userservice.config.SecurityConfig;
import com.example.userservice.exception.EmailAlreadyExistsException;
import com.example.userservice.exception.UserIsAlreadyDeletedException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.model.dto.UserCreateDTO;
import com.example.userservice.model.dto.UserResponseDTO;
import com.example.userservice.model.enums.Role;
import com.example.userservice.model.enums.State;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import com.example.userservice.security.JwtAuthenticationFilter;
import com.example.userservice.security.JwtUtils;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@AutoConfigureMockMvc
@ActiveProfiles("test")
@WebMvcTest(UserController.class) 
@Import(SecurityConfig.class) 
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuthenticationProvider authenticationProvider; 

    @MockitoBean 
    private UserService userService; 

    @MockitoBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean(name = "userDetailsServiceImpl")
    private UserDetailsService userDetailsService; 

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain filterChain = invocation.getArgument(2);
            filterChain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    //createUser
    @Test
    @WithMockUser 
    @DisplayName("Debe retornar 201 al crear un user válido")
    void createUserTest() throws Exception {
        String userJson = """
                {
                    "firstName": "Geronimo",
                    "lastName": "Langenheim",
                    "email": "geronimo@email.com",
                    "password": "Password123!",
                    "birthDate": "19/04/2004",
                    "role": "USER"
                }
                """;
        
        mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser 
    @DisplayName("Debe retornar 400 cuando los datos de entrada son inválidos (Password débil)")
    void createUser_BadRequest_InvalidPassword() throws Exception {
        String userJson = """
                {
                    "firstName": "Geronimo",
                    "lastName": "Langenheim",
                    "email": "geronimo@email.com",
                    "password": "123", 
                    "birthDate": "19/04/2004",
                    "role": "USER"
                }
                """;
        mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    @WithMockUser 
    @DisplayName("Debe retornar 409 cuando el email ya se encuentra registrado")
    void createUser_Conflict_EmailExists() throws Exception {
        String userJson = """
                {
                    "firstName": "Geronimo",
                    "lastName": "Langenheim",
                    "email": "existente@email.com",
                    "password": "Password123!",
                    "birthDate": "19/04/2004",
                    "role": "USER"
                }
                """;
        
        doThrow(new EmailAlreadyExistsException())
            .when(userService).createUser(any(UserCreateDTO.class));

        mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El email ya se encuentra registrado en el sistema."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    //getUser:
    @Test
    @WithMockUser
    @DisplayName("Debe retornar 200 al obtener un user por ID válido")
    void getUser_Success() throws Exception {
        Long idUser = 1L;
        
        UserResponseDTO mockResponse = new UserResponseDTO(
            idUser,
            "Geronimo",
            "Langenheim",
            "geronimo@email.com",
            LocalDate.of(2004, 4, 19), // 19/04/2004
            Role.USER,
            State.ACTIVE,
            List.of(101L, 102L) 
        );

        when(userService.getUser(idUser)).thenReturn(mockResponse);

        mockMvc.perform(get("/users/{idUser}", idUser)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.id").value(idUser))
                .andExpect(jsonPath("$.firstName").value("Geronimo"))
                .andExpect(jsonPath("$.lastName").value("Langenheim"))
                .andExpect(jsonPath("$.email").value("geronimo@email.com"))
                .andExpect(jsonPath("$.birthDate").value("19/04/2004")) 
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.state").value("ACTIVE"))
                .andExpect(jsonPath("$.orderIds[0]").value(101))
                .andExpect(jsonPath("$.orderIds[1]").value(102));
    }

    @Test
    @WithMockUser
    @DisplayName("Debe retornar 404 cuando el usuario no existe")
    void getUser_NotFound() throws Exception {
        Long idInexistente = 99L;
        
        when(userService.getUser(idInexistente)).thenThrow(new UserNotFoundException());

        mockMvc.perform(get("/users/{idUser}", idInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound()) 
                .andExpect(jsonPath("$.message").exists()); 
    }

    //deleteUser: 
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe retornar 204 al borrar un usuario")
    void deleteUser() throws Exception {
        Long idUser = 1L;

        mockMvc.perform(delete("/users/{idUser}", idUser)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe retornar 404 al borrar un usuario que no existe")
    void deleteUser_NotFound() throws Exception {
        Long idInexistente = 99L;

        doThrow(new UserNotFoundException())
            .when(userService).deleteUser(idInexistente);

        mockMvc.perform(delete("/users/{idUser}", idInexistente)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuario no encontrado."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe retornar 410 al borrar un usuario que ya fue eliminado anteriormente")
    void deleteUser_IsGone() throws Exception {
        Long idInexistente = 99L;

        doThrow(new UserIsAlreadyDeletedException())
            .when(userService).deleteUser(idInexistente);

        mockMvc.perform(delete("/users/{idUser}", idInexistente)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.message").value("La cuenta se encuentra inhabilitada.")) 
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    //updateUser:
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe retornar 200 al editar un usuario exitosamente")
    void updateUser() throws Exception {
        Long idUser = 1L;
        UserCreateDTO userCreateDTO = new UserCreateDTO(
            "Geronimo",
            "Langenheim",
            "geronimo@email.com",
            "Password123!",
            LocalDate.of(2004, 4, 19), 
            Role.ADMIN
        );
        UserResponseDTO mockResponse = new UserResponseDTO(
            idUser,
            "Geronimo",
            "Langenheim",
            "geronimo@email.com",
            LocalDate.of(2004, 4, 19),
            Role.ADMIN,
            State.ACTIVE,
            List.of(101L, 102L) 
        );

        when(userService.editUser(idUser, userCreateDTO)).thenReturn(mockResponse);

        mockMvc.perform(put("/users/{idUser}", idUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idUser))
                .andExpect(jsonPath("$.firstName").value("Geronimo"))
                .andExpect(jsonPath("$.email").value("geronimo@email.com"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe retornar 400 al editar un usuario por datos de entrada invalidos")
    void updateUser_data_invalid() throws Exception {
        Long idUser = 1L;
        UserCreateDTO userCreateDTO = new UserCreateDTO(
            "Geronimo",
            "Langenheim",
            "geronimo", 
            "Password123!",
            LocalDate.of(2004, 4, 19), 
            Role.ADMIN
        );

        mockMvc.perform(put("/users/{idUser}", idUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error de validación"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").exists()) 
                .andExpect(jsonPath("$.errors.email").exists());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe retornar 404 al no encontrar el id del usuario a editar")
    void updateUser_idNotValid() throws Exception {
        Long idUserInexistente = 99L;
        UserCreateDTO userCreateDTO = new UserCreateDTO(
            "Geronimo",
            "Langenheim",
            "geronimo@email.com", 
            "Password123!",
            LocalDate.of(2004, 4, 19), 
            Role.ADMIN
        );

        when(userService.editUser(idUserInexistente, userCreateDTO)).thenThrow(new UserNotFoundException());

        mockMvc.perform(put("/users/{idUser}", idUserInexistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Usuario no encontrado."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe retornar 409 al detectar que el email es duplicado")
    void updateUser_emailduplicated() throws Exception {
        Long idUser = 1L;
        UserCreateDTO userCreateDTO = new UserCreateDTO(
            "Geronimo",
            "Langenheim",
            "geronimo@email.com", 
            "Password123!",
            LocalDate.of(2004, 4, 19), 
            Role.ADMIN
        );

        when(userService.editUser(idUser, userCreateDTO)).thenThrow(new EmailAlreadyExistsException());

        mockMvc.perform(put("/users/{idUser}", idUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El email ya se encuentra registrado en el sistema."))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

}
