package com.airgear.telegram.bot;

import org.telegram.telegrambots.meta.api.objects.Update;

public class StartMessageHandler implements MessageHandler {

    @Override
    public void handle(Update update, TelegramBot bot) {
        long chatId = update.getMessage().getChatId();
        if (bot.isAwaitingAnswer()) {
            String messageText = update.getMessage().getText();
            if (messageText.equals("7")) {
                bot.sendResponse(chatId, "Відповідь правильна! Тепер ви можете користуватись ботом. Введіть ID оголошення для отримання інформації.");
                bot.setAwaitingAnswer(false);
            } else {
                bot.sendResponse(chatId, "Неправильна відповідь. Спробуйте ще раз: 16 - 9 = ?");
            }
        } else {
            bot.sendResponse(chatId, "Привіт! Це бот для отримання оголошень. Щоб переконатись, що ви не бот, будь ласка, дайте відповідь на наступне питання: 16 - 9 = ?");
            bot.setAwaitingAnswer(true);
        }
    }

    @Override
    public boolean canHandle(Update update) {
        return update.getMessage().getText().startsWith("/start");
    }
}

