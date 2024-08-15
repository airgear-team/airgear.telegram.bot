package com.airgear.telegram.service;

import com.airgear.model.TelegramUserSession;
import com.airgear.telegram.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSessionService {
    private final UserSessionRepository userSessionRepository;

    public void saveSession(TelegramUserSession session) {
        userSessionRepository.save(session);
    }

    public List<TelegramUserSession> getAllSessions() {
        return userSessionRepository.findAll();
    }
}
