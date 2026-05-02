package com.example.userservice.controller;

import com.example.userservice.model.dto.ReportCreateDTO;
import com.example.userservice.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Endpoints para la gestión de reportes")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Crea un nuevo reporte", description = "Crea un reporte de un usuario a otro")
    @ApiResponse(responseCode = "201", description = "Reporte creado exitosamente")
	@ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
	@ApiResponse(responseCode = "409", description = "El usuario ya ha realizado una denuncia previa contra este perfil")
    @PostMapping
    public ResponseEntity<Void> createReport(@Valid @RequestBody ReportCreateDTO reportCreateDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        reportService.addReport(reportCreateDTO, email);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}