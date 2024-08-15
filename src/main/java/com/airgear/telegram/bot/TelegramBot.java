package com.airgear.telegram.bot;

import com.airgear.model.TelegramUserSession;
import com.airgear.telegram.dto.GoodsResponse;
import com.airgear.telegram.service.GoodsService;
import com.airgear.telegram.service.UserService;
import com.airgear.telegram.service.UserSessionService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegrambots.bot.username}")
    private String botUsername;

    @Value("${telegrambots.bot.token}")
    private String botToken;

    private boolean awaitingAnswer = false;
    private String searchContext = "";
    private List<GoodsResponse> searchResults = new ArrayList<>();
    private int currentSearchIndex = 0;

    private final Map<Long, String> userSessions = new HashMap<>();

    private final GoodsService goodsService;
    private final UserService userService;
    private final UserSessionService userSessionService;
    private final MessageHandler[] messageHandlers;

    @PostConstruct
    private void initUserSessions() {
        List<TelegramUserSession> sessions = userSessionService.getAllSessions();
        for (TelegramUserSession session : sessions) {
            userSessions.put(session.getChatId(), session.getSessionData());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            for (MessageHandler handler : messageHandlers) {
                if (handler.canHandle(update, this)) {
                    handler.handle(update, this);
                    return;
                }
            }
        }
    }

    public void sendResponse(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendReplyKeyboard(long chatId, String text, List<List<String>> options) {
        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        for (List<String> rowOptions : options) {
            KeyboardRow row = new KeyboardRow();
            for (String option : rowOptions) {
                row.add(new KeyboardButton(option));
            }
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

    public void sendContactRequest(long chatId) {
        KeyboardButton contactButton = new KeyboardButton("Поділитися номером");
        contactButton.setRequestContact(true);

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(contactButton);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow));

        SendMessage message = SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text("Натисніть кнопку нижче, щоб поділитися своїм номером телефону:")
                .replyMarkup(keyboardMarkup)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendOptionsMenu(long chatId) {
        List<List<String>> options = Arrays.asList(
                Arrays.asList("Пошук за ID", "Пошук за словами", "Створити товар")
        );
        sendReplyKeyboard(chatId, "Оберіть опцію для пошуку:", options);
    }

    public void addUserSession(long chatId, String sessionData) {
        userSessions.put(chatId, sessionData);
        userSessionService.saveSession(new TelegramUserSession(chatId, sessionData));
    }

    public String getUserSession(long chatId) {
        return userSessions.get(chatId);
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
