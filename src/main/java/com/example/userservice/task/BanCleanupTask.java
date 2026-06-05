package com.example.userservice.task;

import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.example.userservice.model.entity.User;
import com.example.userservice.model.enums.State;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BanCleanupTask {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void autoUnbanUsers() {

        log.info("Ejecutando tarea de desbaneo automático...");
        List<User> expiredBans = userRepository.findExpiredBans(State.BANNED);

        if (expiredBans.isEmpty()) {
            log.info("No se encontraron baneos expirados.");
            return;
        }

        expiredBans.forEach(user -> {
            user.setState(State.ACTIVE);
            user.setBanUntil(null);
            log.info("El usuario {} ha sido desbaneado automáticamente.", user.getEmail());
        });
        log.info("Tarea de desbaneo automático finalizada. Total de usuarios desbaneados: {}", expiredBans.size());
    }

}
