package com.airgear.telegram.bot;

import com.airgear.telegram.dto.GoodsResponse;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.Collections;
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
            sendNextGoods(chatId, bot);
            return;
        }

        if (bot.getSearchContext().equals("searchByWords")) {
            List<GoodsResponse> searchResults = bot.getGoodsService().searchGoodsByKeyword(messageText);
            bot.setSearchResults(searchResults);
            bot.setCurrentSearchIndex(0);
            sendNextGoods(chatId, bot);
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

    private void sendNextGoods(long chatId, TelegramBot bot) {
        List<GoodsResponse> searchResults = bot.getSearchResults();
        int currentIndex = bot.getCurrentSearchIndex();

        if (currentIndex < searchResults.size()) {
            GoodsResponse goods = searchResults.get(currentIndex);
            bot.sendResponse(chatId, goods.toString());
            bot.setCurrentSearchIndex(currentIndex + 1);

            List<String> options = Arrays.asList("Наступний", "Назад", "Головне меню");
            if (currentIndex + 1 >= searchResults.size()) {
                options = Arrays.asList("Назад", "Головне меню");
            }

            bot.sendReplyKeyboard(chatId, "Натисніть кнопку для наступної дії:", options);
        } else {
            bot.sendResponse(chatId, "Більше немає результатів.");
            sendMainMenu(chatId, bot);
        }
    }
}
