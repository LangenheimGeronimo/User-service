package com.example.userservice.service;

import com.example.userservice.exception.AlreadyReportedException;
import com.example.userservice.exception.SelfReportException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.model.dto.ReportCreateDTO;
import com.example.userservice.model.entity.Report;
import com.example.userservice.model.entity.User;
import com.example.userservice.model.enums.Role;
import com.example.userservice.model.enums.State;
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
    private static final int MAX_REPORTS_BEFORE_BAN = 3;

    @Transactional
    public void addReport(ReportCreateDTO dto, String reporterEmail) {
        // 1. Buscamos al denunciante (desde el email)
        User reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new UserNotFoundException("Usuario denunciante no encontrado"));

        // 2. Buscamos al denunciado (desde el ID del DTO)
        User reported = userRepository.findById(dto.reportedUserId())
                .orElseThrow(() -> new UserNotFoundException("Usuario denunciado no encontrado"));

        validateReporte(reporter, reported);
        Report report = Report.builder()
                .reporterUserId(reporter.getId()) 
                .reportedUserId(reported.getId()) 
                .reason(dto.reason())
                .createdAt(LocalDateTime.now())
                .build();

        reportRepository.save(report);
        checkAndApplyBan(reported);
    }

    private void validateReporte(User reporter, User reported) {
        if (reporter.getId().equals(reported.getId())) { 
            throw new SelfReportException(); 
        }
        if (reportRepository.existsByReporterUserIdAndReportedUserId(reporter.getId(), reported.getId())) {
            throw new AlreadyReportedException();
        }
    }

    private void checkAndApplyBan(User user) {
        if (user.getRole() == Role.ADMIN) {
            return;
        }

        long reportCount = reportRepository.countByReportedUserId(user.getId());
        
        if (reportCount >= MAX_REPORTS_BEFORE_BAN) {
            user.setState(State.BANNED);
            userRepository.save(user);
        }
    }

}