package com.airgear.telegram.bot;

import com.airgear.telegram.dto.GoodsResponse;
import com.airgear.telegram.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class SearchByWordsHandler implements MessageHandler {

    private final ImageService imageService;

    @Autowired
    public SearchByWordsHandler(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    public void handle(Update update, TelegramBot bot) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        if (!isAuthorized(bot, chatId)) {
            bot.sendResponse(chatId, "Вам потрібно авторизуватися для використання цієї функції.");
            return;
        }

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
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            if ("Пошук за словами".equals(messageText)) {
                return true;
            }
        }
        String searchContext = bot.getSearchContext();
        return "searchByWords".equals(searchContext);
    }

    private boolean isAuthorized(TelegramBot bot, long chatId) {
        String sessionData = bot.getUserSession(chatId);
        return sessionData != null && !sessionData.startsWith("awaiting_");
    }

    private void sendMainMenu(long chatId, TelegramBot bot) {
        List<List<String>> options = Arrays.asList(
                Arrays.asList("Пошук за ID", "Пошук за словами")
        );
        bot.sendReplyKeyboard(chatId, "Оберіть опцію для пошуку:", options);
        bot.setSearchContext("");
    }

    private void sendNextGoods(long chatId, TelegramBot bot, int direction) {
        List<GoodsResponse> searchResults = bot.getSearchResults();
        int currentIndex = bot.getCurrentSearchIndex() + direction;

        if (currentIndex >= 0 && currentIndex < searchResults.size()) {
            GoodsResponse goods = searchResults.get(currentIndex);
            bot.sendResponse(chatId, goods.toFormattedString());
            try {
                Optional<byte[]> imageBytes = imageService.getFirstImageBytesByGoodsId(goods.getId());
                if (imageBytes.isPresent()) {
                    sendPhoto(chatId, bot, imageBytes.get());
                }
            } catch (IOException e) {
                bot.sendResponse(chatId, "Не вдалося завантажити зображення.");
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
            bot.setCurrentSearchIndex(currentIndex);

            List<List<String>> options = getLists(currentIndex, searchResults);

            bot.sendReplyKeyboard(chatId, "Виберіть опцію:", options);
        } else {
            bot.sendResponse(chatId, "Більше немає результатів.");
            sendMainMenu(chatId, bot);
        }
    }

    private static List<List<String>> getLists(int currentIndex, List<GoodsResponse> searchResults) {
        List<List<String>> options;
        if (currentIndex == 0 && currentIndex == searchResults.size() - 1) {
            options = Arrays.asList(
                    Arrays.asList("Назад", "Головне меню")
            );
        } else if (currentIndex == 0) {
            options = Arrays.asList(
                    Arrays.asList("Наступний"),
                    Arrays.asList("Назад", "Головне меню")
            );
        } else if (currentIndex == searchResults.size() - 1) {
            options = Arrays.asList(
                    Arrays.asList("Попередній"),
                    Arrays.asList("Назад", "Головне меню")
            );
        } else {
            options = Arrays.asList(
                    Arrays.asList("Попередній", "Наступний"),
                    Arrays.asList("Назад", "Головне меню")
            );
        }
        return options;
    }

    private void sendPhoto(long chatId, TelegramBot bot, byte[] imageBytes) throws TelegramApiException {
        InputFile inputFile = new InputFile(new ByteArrayInputStream(imageBytes), "image.jpg");
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(String.valueOf(chatId))
                .photo(inputFile)
                .build();
        bot.execute(sendPhoto);
    }
}
