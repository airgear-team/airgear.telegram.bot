package com.airgear.telegram.bot;

import com.airgear.telegram.dto.GoodsResponse;
import com.airgear.telegram.service.GoodsService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegrambots.bot.username}")
    private String botUsername;

    @Value("${telegrambots.bot.token}")
    private String botToken;

    private boolean awaitingAnswer = false;
    private String searchContext = "";
    private List<GoodsResponse> searchResults = new ArrayList<>();
    private int currentSearchIndex = 0;

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
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendReplyKeyboard(long chatId, String text, List<String> options) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        for (String option : options) {
            KeyboardRow row = new KeyboardRow();
            row.add(option);
            keyboard.add(row);
        }

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
