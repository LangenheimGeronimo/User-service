package com.example.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.userservice.model.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    // Cuenta cuántos reportes tiene un usuario específico
    // (Asegurate que en tu entidad Report el campo se llame "reportedUser")
    long countByReportedUserId(Long reportedUserId);

    // Evita que un usuario denuncie dos veces al mismo
    // Spring navegará por la entidad: reporterUser -> id y reportedUser -> id
    boolean existsByReporterUserIdAndReportedUserId(Long reporterUserId, Long reportedUserId);
}