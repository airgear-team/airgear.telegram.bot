package com.airgear.telegram.bot;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Data
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${telegrambots.bot.username}")
    private String botUsername;

    @Value("${telegrambots.bot.token}")
    private String botToken;

    private boolean awaitingAnswer = false;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("/start")) {
                sendResponse(chatId, "Привіт! Це бот для отримання оголошень. Щоб переконатись, що ви не бот, будь ласка, дайте відповідь на наступне питання: 16 - 9 = ?");
                awaitingAnswer = true;
            } else if (awaitingAnswer) {
                if (messageText.equals("7")) {
                    sendResponse(chatId, "Відповідь правильна! Тепер ви можете користуватись ботом.");
                    awaitingAnswer = false;
                } else {
                    sendResponse(chatId, "Неправильна відповідь. Спробуйте ще раз: 16 - 9 = ?");
                }
            } else {
                sendResponse(chatId, "Будь ласка, введіть команду /start, щоб розпочати.");
            }
        }
    }

    private void sendResponse(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
