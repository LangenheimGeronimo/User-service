package com.example.userservice.service.impl;

import com.example.userservice.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j 
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendStatusChangeNotification(String userEmail, String newState, String reason) {
        log.info("SIMULANDO ENVÍO DE EMAIL A: {}", userEmail);
        log.info("Asunto: Tu estado de cuenta ha cambiado a {}", newState);
        log.info("Cuerpo: Estimado usuario, tu cuenta ahora está {}. Motivo: {}", newState, reason);
        log.info("---------------------------------------------------------");
    }
}
