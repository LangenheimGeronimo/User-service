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
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
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
        User reporter = userRepository.findByEmail(reporterEmail).orElseThrow(UserNotFoundException::new);
        User reported = userRepository.findById(dto.reportedUserId()).orElseThrow(UserNotFoundException::new);

        validateReport(reporter, reported);

        Report report = reportMapper.toEntity(dto);
        
        report.setReporterUserId(reporter.getId()); 

        reportRepository.save(report);
        reEvaluateUserStatus(reported.getId());
    }
    

    private void validateReport(User reporter, User reported) {
        if (reporter.getId().equals(reported.getId())) { 
            throw new SelfReportException(); 
        }
        if (reportRepository.existsByReporterUserIdAndReportedUserId(reporter.getId(), reported.getId())) {
            throw new AlreadyReportedException();
        }
    }

    @Transactional
    public void deleteReportById(Long id) {
        Report report = reportRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        Long reportedUserId = report.getReportedUserId();
        reportRepository.delete(report);
        reportRepository.flush();
        reEvaluateUserStatus(reportedUserId);
    }

    private void reEvaluateUserStatus(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if (user.getRole() == Role.ADMIN) return;

        long currentReports = reportRepository.countByReportedUserId(userId);
        State oldState = user.getState();

        if (currentReports >= MAX_REPORTS_BEFORE_BAN && oldState != State.BANNED) {
            LocalDateTime banUntil = LocalDateTime.now().plusDays(BAN_DAYS);
            user.setBanUntil(banUntil);
            updateUserStatus(user, State.BANNED, "Automatic ban: reports reached " + currentReports);
        } 
        else if (currentReports < MAX_REPORTS_BEFORE_BAN && oldState == State.BANNED) {
            user.setBanUntil(null);
            updateUserStatus(user, State.ACTIVE, "Automatic reactivation: reports dropped to " + currentReports);
        }
    }

    private void updateUserStatus(User user, State newState, String reason) {
        State oldState = user.getState();
        user.setState(newState);
        userRepository.save(user);

        userStatusHistoryRepository.save(UserStatusHistory.builder()
                .userId(user.getId())
                .previousState(oldState)
                .newState(newState)
                .reason(reason)
                .build());

        notificationService.sendStatusChangeNotification(user.getEmail(), newState.name(), reason);
    }

    
    

}