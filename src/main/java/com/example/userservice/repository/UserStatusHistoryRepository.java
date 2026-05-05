package com.example.userservice.repository;

import com.example.userservice.model.entity.UserStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserStatusHistoryRepository extends JpaRepository<UserStatusHistory, Long> {
    
    List<UserStatusHistory> findByUserId(Long userId);
}