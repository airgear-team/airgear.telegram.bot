package com.airgear.telegram.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@Component
public class StartMessageHandler implements MessageHandler {

    @Override
    public void handle(Update update, TelegramBot bot) {
        Message message = update.getMessage();
        long chatId = message.getChatId();
        String messageText = message.getText();

        if (bot.isAwaitingAnswer()) {
            if (messageText.equals("7")) {
                bot.sendResponse(chatId, "Відповідь правильна! Тепер ви можете обрати, що саме робити далі.");
                bot.setAwaitingAnswer(false);
                sendOptionsMenu(chatId, bot);
            } else {
                bot.sendResponse(chatId, "Неправильна відповідь. Спробуйте ще раз: 16 - 9 = ?");
                sendReplyKeyboard(chatId, bot);
            }
        } else {
            bot.sendResponse(chatId, "Привіт! Це бот для отримання оголошень. Щоб переконатись, що ви не бот, будь ласка, дайте відповідь на наступне питання: 16 - 9 = ?");
            bot.setAwaitingAnswer(true);
            sendReplyKeyboard(chatId, bot);
        }
    }

    @Override
    public boolean canHandle(Update update, TelegramBot bot) {
        Message message = update.getMessage();
        return bot.isAwaitingAnswer() || (message != null && message.getText().startsWith("/start"));
    }

    private void sendReplyKeyboard(long chatId, TelegramBot bot) {
        List<List<String>> options = Arrays.asList(
                Arrays.asList("6", "7"),
                Arrays.asList("8", "9")
        );
        bot.sendReplyKeyboard(chatId, "Оберіть відповідь:", options);
    }

    private void sendOptionsMenu(long chatId, TelegramBot bot) {
        List<List<String>> options = Arrays.asList(
                Arrays.asList("Пошук за ID", "Пошук за словами")
        );
        bot.sendReplyKeyboard(chatId, "Оберіть опцію для пошуку:", options);
    }
}
