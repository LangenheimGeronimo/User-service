package com.example.userservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.userservice.exception.AlreadyReportedException;
import com.example.userservice.exception.SelfReportException;
import com.example.userservice.model.dto.ReportCreateDTO;
import com.example.userservice.security.JwtUtils;
import com.example.userservice.service.ReportService;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(ReportController.class) 
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean 
    private ReportService reportService; 

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsService userDetailsService; 

    @SuppressWarnings("null")
    @Test
    @WithMockUser(username = "gero@test.com")
    @DisplayName("Debe retornar 201 al crear un reporte válido")
    void createReportTest() throws Exception {
        String reportJson = """
                {
                    "reporterId": 1,
                    "reportedId": 2,
                    "reason": "Comportamiento inadecuado"
                }
                """;

        mockMvc.perform(post("/api/v1/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reportJson))
                .andExpect(status().isCreated());
    }

    @SuppressWarnings("null")
    @Test
    @WithMockUser(username = "gero@test.com")
    @DisplayName("Debe retornar 409 cuando el usuario ya fue reportado previamente")
    void createReport_AlreadyReported() throws Exception {
        String reportJson = """
                {
                    "reporterId": 1,
                    "reportedId": 2,
                    "reason": "Spam"
                }
                """;
        
        doThrow(new AlreadyReportedException())
            .when(reportService).addReport(any(ReportCreateDTO.class), anyString());

        mockMvc.perform(post("/api/v1/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reportJson))
                .andExpect(status().isConflict()) 
                .andExpect(jsonPath("$.message").value("El usuario ya ha realizado una denuncia previa contra este perfil"));
    }

    @SuppressWarnings("null")
    @Test
    @WithMockUser(username = "gero@test.com")
    @DisplayName("Debe retornar 400 cuando un usuario intenta reportarse a sí mismo")
    void createReport_SelfReport() throws Exception {
        String selfReportJson = """
                {
                    "reporterId": 1,
                    "reportedId": 1,
                    "reason": "Test de autoreporte"
                }
                """;

        doThrow(new SelfReportException())
            .when(reportService).addReport(any(ReportCreateDTO.class), anyString());

        mockMvc.perform(post("/api/v1/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(selfReportJson))
                .andExpect(status().isBadRequest()) 
                .andExpect(jsonPath("$.message").value("No puedes reportarte a ti mismo"));
    }

    @Test
    @WithMockUser(roles = "USER") 
    @DisplayName("Debe retornar 403 al intentar eliminar un reporte sin ser ADMIN")
    void deleteReport_Forbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/reports/1"))
                .andExpect(status().isForbidden());
    }


}