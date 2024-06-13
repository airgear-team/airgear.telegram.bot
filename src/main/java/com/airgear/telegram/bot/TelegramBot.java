package com.airgear.telegram.bot;

import com.airgear.telegram.service.GoodsService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private MessageHandler[] messageHandlers;

    @Override
    public void onUpdateReceived(Update update) {
        for (MessageHandler handler : messageHandlers) {
            if (handler.canHandle(update, this)) {
                handler.handle(update, this);
                return;
            }
        }
    }

    public void sendResponse(long chatId, String text) {
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
