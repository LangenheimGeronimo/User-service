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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import com.example.userservice.exception.AlreadyReportedException;
import com.example.userservice.exception.ResourceNotFoundException;
import com.example.userservice.exception.SelfReportException;
import com.example.userservice.model.dto.ReportCreateDTO;
import com.example.userservice.security.JwtAuthenticationFilter;
import com.example.userservice.security.JwtUtils;
import com.example.userservice.service.ReportService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@WebMvcTest(ReportController.class) 
@Import(SecurityConfig.class) 
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private AuthenticationProvider authenticationProvider; 

    @MockitoBean 
    private ReportService reportService; 

    @MockitoBean
    private JwtUtils jwtUtils;

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

    // --- MÉTODOS DE CREACIÓN (POST) ---

    @Test
    @WithMockUser(username = "gero@test.com")
    @DisplayName("Debe retornar 201 al crear un reporte válido")
    void createReportTest() throws Exception {
        String reportJson = """
                {
                    "reason": "Comportamiento inadecuado",
                    "reportedUserId": 2
                }
                """;
        
        mockMvc.perform(post("/api/v1/reports")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(reportJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "gero@test.com")
    @DisplayName("Debe retornar 409 cuando el usuario ya fue reportado previamente")
    void createReport_AlreadyReported() throws Exception {
        String reportJson = """
                {
                    "reason": "Spam",
                    "reportedUserId": 2
                }
                """;
        
        doThrow(new AlreadyReportedException())
            .when(reportService).addReport(any(ReportCreateDTO.class), anyString());

        mockMvc.perform(post("/api/v1/reports")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(reportJson))
                .andExpect(status().isConflict()) 
                .andExpect(jsonPath("$.message").value("El usuario ya ha realizado una denuncia previa contra este perfil."));
    }

    @Test
    @WithMockUser(username = "gero@test.com")
    @DisplayName("Debe retornar 400 cuando un usuario intenta reportarse a sí mismo")
    void createReport_SelfReport() throws Exception {
        String selfReportJson = """
                {
                    "reason": "Test de autoreporte",
                    "reportedUserId": 2
                }
                """;

        doThrow(new SelfReportException())
            .when(reportService).addReport(any(ReportCreateDTO.class), anyString());

        mockMvc.perform(post("/api/v1/reports")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(selfReportJson))
                .andExpect(status().isBadRequest()) 
                .andExpect(jsonPath("$.message").value("Un usuario no puede denunciarse a sí mismo."));
    }

    // --- MÉTODOS DE ELIMINACIÓN (DELETE) ---

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe retornar 204 al eliminar un reporte existosamente como ADMIN")
    void deleteReport_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/reports/{id}", 1L)
                .with(csrf()))
                .andExpect(status().isNoContent()); 
    }

    @Test
    @WithMockUser(roles = "USER") 
    @DisplayName("Debe retornar 403 al intentar eliminar un reporte sin ser ADMIN")
    void deleteReport_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/reports/{id}", 1L) // Usamos {id} para consistencia
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Debe retornar 404 cuando se intenta eliminar un reporte que no existe")
    void deleteReport_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException())
            .when(reportService).deleteReportById(999L);

        mockMvc.perform(delete("/api/v1/reports/{id}", 999L)
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
}