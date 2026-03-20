package com.example.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.userservice.model.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    // Cuenta cuántos reportes tiene un usuario específico
    long countByReportedUserId(Long reportedUserId);

    // Evita que un usuario denuncie dos veces al mismo
    boolean existsByReporterUserIdAndReportedUserId(Long reporterUserId, Long reportedUserId);

    void save(Optional<Report> report);
}
