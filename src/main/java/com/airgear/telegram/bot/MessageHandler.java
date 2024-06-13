package com.airgear.telegram.bot;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageHandler {
    void handle(Update update, TelegramBot bot);
    boolean canHandle(Update update, TelegramBot bot);
}
