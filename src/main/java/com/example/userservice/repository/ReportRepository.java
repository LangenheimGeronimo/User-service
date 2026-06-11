package com.example.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.userservice.model.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
    
    // Consulta optimizada para la auditoría de estado y ejecución del ban automático.
    // Se asume la existencia de un índice sobre 'reported_user_id' para acelerar el conteo.
    long countByReportedUserId(Long reportedUserId);

    // Validación de unicidad de denuncias para evitar spam o duplicados.
    // Se apoya en un índice compuesto (reporter_user_id, reported_user_id) a nivel base de datos.
    boolean existsByReporterUserIdAndReportedUserId(Long reporterUserId, Long reportedUserId); 
}