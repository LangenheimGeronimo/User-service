package com.example.userservice.service;

import com.example.userservice.exception.AlreadyReportedException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.model.dto.ReportCreateDTO;
import com.example.userservice.model.entity.Report;
import com.example.userservice.model.entity.User;
import com.example.userservice.repository.ReportRepository;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addReport(ReportCreateDTO dto) {
        // Buscamos al denunciante
        User reporter = userRepository.findById(dto.reporterUserId())
                .orElseThrow(() -> new UserNotFoundException("Usuario denunciante no encontrado"));

        // Buscamos al denunciado (el reportedUserId de tu DTO)
        User reported = userRepository.findById(dto.reportedUserId())
                .orElseThrow(() -> new UserNotFoundException("Usuario denunciado no encontrado"));

        // Validación de duplicados
       if (reportRepository.existsByReporterUserIdAndReportedUserId(dto.reporterUserId(), dto.reportedUserId())) {
            throw new AlreadyReportedException("Ya has realizado una denuncia contra este usuario");
        }

        Report report = Report.builder()
            .reporterUserId(reporter.getId()) 
            .reportedUserId(reported.getId()) 
            .reason(dto.reason())
            .createdAt(LocalDateTime.now())
            .build();

        reportRepository.save(report);
    }
}