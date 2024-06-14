package com.airgear.telegram.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.Collections;

@Component
public class SearchByWordsHandler implements MessageHandler {

    @Override
    public void handle(Update update, TelegramBot bot) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        if (messageText.equals("Назад")) {
            sendMainMenu(chatId, bot);
            return;
        }

        if (bot.getSearchContext().equals("searchByWords")) {
            // Тут потрібно реалізувати логіку пошуку за ключовими словами
            bot.sendResponse(chatId, "Пошук за ключовими словами ще не реалізований.");
            sendBackButton(chatId, bot);
        } else {
            bot.sendResponse(chatId, "Введіть ключові слова для пошуку:");
            bot.setSearchContext("searchByWords");
        }
    }

    @Override
    public boolean canHandle(Update update, TelegramBot bot) {
        return update.getMessage().getText().equals("Пошук за словами") || bot.getSearchContext().equals("searchByWords");
    }

    private void sendMainMenu(long chatId, TelegramBot bot) {
        bot.sendReplyKeyboard(chatId, "Оберіть опцію для пошуку:", Arrays.asList("Пошук за ID", "Пошук за словами"));
        bot.setSearchContext("");
    }

    private void sendBackButton(long chatId, TelegramBot bot) {
        bot.sendReplyKeyboard(chatId, "Натисніть кнопку, щоб повернутися назад:", Collections.singletonList("Назад"));
    }
}
