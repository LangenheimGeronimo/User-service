package com.example.userservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import com.example.userservice.repository.UserStatusHistoryRepository;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserStatusHistoryRepository userStatusHistoryRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReportService reportService;

    @Test
    void addReport_ShouldSaveReportAndNotBan_WhenReportsUnderLimit() {
        String reporterEmail = "reporter@test.com";
        ReportCreateDTO dto = new ReportCreateDTO("Spam", 2L);
        User reporter = User.builder().id(1L).email(reporterEmail).build();
        User reported = User.builder().id(2L).role(Role.USER).state(State.ACTIVE).build();

        when(userRepository.findByEmail(reporterEmail)).thenReturn(Optional.of(reporter));
        when(userRepository.findById(2L)).thenReturn(Optional.of(reported));
        when(reportRepository.existsByReporterUserIdAndReportedUserId(1L, 2L)).thenReturn(false);
        when(reportRepository.countByReportedUserId(2L)).thenReturn(1L); 

        reportService.addReport(dto, reporterEmail);

        verify(reportRepository, times(1)).save(any(Report.class));
        verify(userRepository, never()).save(any(User.class)); // No debería banearse aún
        verify(notificationService, never()).sendStatusChangeNotification(any(), any(), any());
    }

    @Test
    void addReport_ShouldThrowSelfReportException_WhenUserReportsHimself() {
        String email = "me@test.com";
        ReportCreateDTO dto = new ReportCreateDTO("Self report", 1L);
        User user = User.builder().id(1L).email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(SelfReportException.class, () -> {
            reportService.addReport(dto, email);
        });

        verify(reportRepository, never()).save(any());
    }

    @Test
    void addReport_ShouldThrowUserNotFoundException_WhenReportedUserDoesNotExist(){
        String email = "me@test.com";
        Long idInexistente = 99L; 
        ReportCreateDTO dto = new ReportCreateDTO("No existe", idInexistente);

        User reporter = User.builder().id(1L).email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(reporter));
        when(userRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            reportService.addReport(dto, email);
        });

        verify(reportRepository, never()).save(any());
    }


    @Test
    void addReport_ShouldThrowException_WhenReporterNotFound() {
        String emailInexistente = "email@test.com";
        Long id = 1L; 
        ReportCreateDTO dto = new ReportCreateDTO("No existe", id);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            reportService.addReport(dto, emailInexistente);
        });

        verify(reportRepository, never()).save(any());
    }


    @Test
    void addReport_ShouldThrowAlreadyReportedException_WhenReportAlreadyExists() {
        String reporterEmail = "reporter@test.com";
        Long reportedId = 2L;
        ReportCreateDTO dto = new ReportCreateDTO("Spam", reportedId);

        User reporter = User.builder().id(1L).email(reporterEmail).build();
        User reported = User.builder().id(reportedId).build();

        when(userRepository.findByEmail(reporterEmail)).thenReturn(Optional.of(reporter));
        when(userRepository.findById(reportedId)).thenReturn(Optional.of(reported));

        when(reportRepository.existsByReporterUserIdAndReportedUserId(1L, 2L)).thenReturn(true);

        assertThrows(AlreadyReportedException.class, () -> {
            reportService.addReport(dto, reporterEmail);
        });

        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    void addReport_ShouldBanUser_WhenReportsReachLimit() {
        String reporterEmail = "reporter@test.com";
        ReportCreateDTO dto = new ReportCreateDTO("Spam", 2L);
        User reporter = User.builder().id(1L).email(reporterEmail).build();
        User reported = User.builder().id(2L).role(Role.USER).state(State.ACTIVE).build();

        when(userRepository.findByEmail(reporterEmail)).thenReturn(Optional.of(reporter));
        when(userRepository.findById(2L)).thenReturn(Optional.of(reported));
        when(reportRepository.existsByReporterUserIdAndReportedUserId(1L, 2L)).thenReturn(false);
        when(reportRepository.countByReportedUserId(2L)).thenReturn(3L); 

        reportService.addReport(dto, reporterEmail);

        verify(reportRepository).save(any(Report.class));
        verify(userRepository).save(reported); 
        
        verify(notificationService).sendStatusChangeNotification(any(), any(), any());
    }

}