package com.airgear.telegram.bot;

import com.airgear.telegram.dto.GoodsResponse;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@Component
public class SearchByWordsHandler implements MessageHandler {

    @Override
    public void handle(Update update, TelegramBot bot) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        if (messageText.equals("Назад")) {
            sendMainMenu(chatId, bot);
            return;
        } else if (messageText.equals("Головне меню")) {
            sendMainMenu(chatId, bot);
            return;
        } else if (messageText.equals("Наступний")) {
            sendNextGoods(chatId, bot, 1);
            return;
        } else if (messageText.equals("Попередній")) {
            sendNextGoods(chatId, bot, -1);
            return;
        }

        if (bot.getSearchContext().equals("searchByWords")) {
            List<GoodsResponse> searchResults = bot.getGoodsService().searchGoodsByKeyword(messageText);
            bot.setSearchResults(searchResults);
            bot.setCurrentSearchIndex(0);
            sendNextGoods(chatId, bot, 0);
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

    private void sendNextGoods(long chatId, TelegramBot bot, int direction) {
        List<GoodsResponse> searchResults = bot.getSearchResults();
        int currentIndex = bot.getCurrentSearchIndex() + direction;

        if (currentIndex >= 0 && currentIndex < searchResults.size()) {
            GoodsResponse goods = searchResults.get(currentIndex);
            bot.sendResponse(chatId, goods.toFormattedString());
            bot.setCurrentSearchIndex(currentIndex);

            List<String> options = Arrays.asList("Попередній", "Наступний", "Назад", "Головне меню");
            if (currentIndex == 0) {
                options = Arrays.asList("Наступний", "Назад", "Головне меню");
            } else if (currentIndex == searchResults.size() - 1) {
                options = Arrays.asList("Попередній", "Назад", "Головне меню");
            }

            bot.sendReplyKeyboard(chatId, "Натисніть кнопку для наступної дії:", options);
        } else {
            bot.sendResponse(chatId, "Більше немає результатів.");
            sendMainMenu(chatId, bot);
        }
    }
}
