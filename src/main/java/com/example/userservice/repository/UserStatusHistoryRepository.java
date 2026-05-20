package com.example.userservice.repository;

import com.example.userservice.model.entity.UserStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface UserStatusHistoryRepository extends JpaRepository<UserStatusHistory, Long> {
    
    // Recupera la trazabilidad completa de los estados de un usuario para auditoría.
    // Se apoya en un índice en la base de datos sobre 'user_id' para optimizar el ordenamiento y búsqueda.
    List<UserStatusHistory> findByUserId(Long userId);
}