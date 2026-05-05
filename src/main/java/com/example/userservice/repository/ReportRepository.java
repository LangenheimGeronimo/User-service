package com.example.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.userservice.model.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    long countByReportedUserId(Long reportedUserId);

    boolean existsByReporterUserIdAndReportedUserId(Long reporterUserId, Long reportedUserId); //CORREGIR
}