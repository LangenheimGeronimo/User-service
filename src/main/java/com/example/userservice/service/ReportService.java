package com.example.userservice.service;

import com.example.userservice.exception.AlreadyReportedException;
import com.example.userservice.exception.ResourceNotFoundException;
import com.example.userservice.exception.SelfReportException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.mapper.ReportMapper;
import com.example.userservice.model.dto.ReportCreateDTO;
import com.example.userservice.model.entity.Report;
import com.example.userservice.model.entity.User;
import com.example.userservice.model.entity.UserStatusHistory;
import com.example.userservice.model.enums.Role;
import com.example.userservice.model.enums.State;
import com.example.userservice.repository.ReportRepository;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.repository.UserStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportMapper reportMapper;
    private final UserStatusHistoryRepository userStatusHistoryRepository;
    private final NotificationService notificationService;
    private static final int MAX_REPORTS_BEFORE_BAN = 3;
    private static final int BAN_DAYS = 7;

    @Transactional
    public void addReport(ReportCreateDTO dto, String reporterEmail) {
        Long reportedUserId = dto.reportedUserId();

        if (reportedUserId == null) {
            throw new IllegalArgumentException("El ID del usuario reportado no puede ser nulo");
        }

        User reporter = userRepository.findByEmail(reporterEmail).orElseThrow(UserNotFoundException::new);
        User reported = userRepository.findById(reportedUserId).orElseThrow(UserNotFoundException::new);

        log.info("Procesando nuevo reporte de {} hacia el usuario ID: {}", reporterEmail, reported.getId());
        validateReport(reporter, reported);

        Report report = reportMapper.toEntity(dto);
        
        report.setReporterUserId(reporter.getId()); 

        reportRepository.save(report);
        log.info("Reporte guardado con éxito. Iniciando reevaluación de estado para el usuario ID: {}", reported.getId());
        reEvaluateUserStatus(reported.getId());
    }
    

    private void validateReport(User reporter, User reported) {
        if (reporter.getId().equals(reported.getId())) { 
            log.warn("Intento de autoreporte bloqueado para el usuario ID: {}", reporter.getId());
            throw new SelfReportException(); 
        }
        if (reportRepository.existsByReporterUserIdAndReportedUserId(reporter.getId(), reported.getId())) {
            log.warn("Usuario ID: {} intentó duplicar un reporte contra el usuario ID: {}", reporter.getId(), reported.getId());
            throw new AlreadyReportedException();
        }
    }

    @Transactional
    public void deleteReportById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del reporte a eliminar no puede ser nulo");
        }
        Report report = reportRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        Long reportedUserId = report.getReportedUserId();
        if (reportedUserId == null) {
            throw new IllegalStateException("El reporte encontrado no tiene un ID de usuario reportado asociado");
        }
        log.info("Removiendo reporte ID: {} que afectaba al usuario ID: {}", id, reportedUserId);
        reportRepository.delete(report); // Asegura el borrado físico antes de contar
        reportRepository.flush();
        reEvaluateUserStatus(reportedUserId);
    }

    private void reEvaluateUserStatus(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("El ID del usuario a reevaluar no puede ser nulo");
        }
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (user.getRole() == Role.ADMIN){
            log.info("El usuario ID: {} es ADMIN. Se omiten las penalizaciones automáticas.", userId);
            return; 
        } 

        long currentReports = reportRepository.countByReportedUserId(userId);
        State oldState = user.getState();

        if (currentReports >= MAX_REPORTS_BEFORE_BAN && oldState != State.BANNED) {
            log.warn("¡LÍMITE ALCANZADO! El usuario ID: {} alcanzó {} reportes. Aplicando baneo.", userId, currentReports);
            LocalDateTime banUntil = LocalDateTime.now().plusDays(BAN_DAYS);
            user.setBanUntil(banUntil);
            updateUserStatus(user, State.BANNED, "Automatic ban: reports reached " + currentReports);
        } 
        else if (currentReports < MAX_REPORTS_BEFORE_BAN && oldState == State.BANNED) {
            log.info("REHABILITACIÓN: Los reportes del usuario ID: {} bajaron a {}. Reactivando cuenta.", userId, currentReports);
            user.setBanUntil(null);
            updateUserStatus(user, State.ACTIVE, "Automatic reactivation: reports dropped to " + currentReports);
        }
    }

    private void updateUserStatus(User user, State newState, String reason) {
        State oldState = user.getState();
        user.setState(newState);
        userRepository.save(user);

        userStatusHistoryRepository.save(java.util.Objects.requireNonNull(
            UserStatusHistory.builder()
                .userId(user.getId())
                .previousState(oldState)
                .newState(newState)
                .reason(reason)
                .build()
        ));
        log.info("Historial de estado registrado para usuario ID: {}. Nuevo estado: {}", user.getId(), newState);   
        notificationService.sendStatusChangeNotification(user.getEmail(), newState.name(), reason);
    }

    
    

}