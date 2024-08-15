package com.airgear.telegram.repository;

import com.airgear.model.TelegramUserSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository extends JpaRepository<TelegramUserSession, Long> {
}
