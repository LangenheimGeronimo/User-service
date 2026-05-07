package com.example.userservice.task;

import java.time.LocalDateTime;
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

        log.info("Running auto-unban task...");
        List<User> usersToUnban = userRepository.findByStateAndBanUntilBefore(State.BANNED, LocalDateTime.now());

        usersToUnban.forEach(user -> {
            user.setState(State.ACTIVE);
            user.setBanUntil(null);
            userRepository.save(user);
            log.info("User {} has been automatically unbanned.", user.getEmail());
        });

    }

}
