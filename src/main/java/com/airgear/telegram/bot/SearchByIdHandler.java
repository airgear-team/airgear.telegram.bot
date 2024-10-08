package com.airgear.telegram.bot;

import com.airgear.telegram.dto.GoodsResponse;
import com.airgear.telegram.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SearchByIdHandler implements MessageHandler {

    private final ImageService imageService;

    @Override
    public void handle(Update update, TelegramBot bot) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        if (!isAuthorized(bot, chatId)) {
            bot.sendResponse(chatId, "Вам потрібно авторизуватися для використання цієї функції.");
            bot.sendContactRequest(chatId);
            return;
        }

        if (messageText.equals("Назад")) {
            sendMainMenu(chatId, bot);
            return;
        }

        if (bot.getSearchContext().equals("searchById")) {
            try {
                Long goodsId = Long.parseLong(messageText);
                GoodsResponse goodsResponseDTO = bot.getGoodsService().getGoodsById(goodsId);

                if (goodsResponseDTO != null) {
                    bot.sendResponse(chatId, goodsResponseDTO.toFormattedString());

                    try {
                        Optional<byte[]> imageBytes = imageService.getFirstImageBytesByGoodsId(goodsId);
                        if (imageBytes.isPresent()) {
                            sendPhoto(chatId, bot, imageBytes.get());
                        }
                    } catch (IOException e) {
                        bot.sendResponse(chatId, "Не вдалося завантажити зображення.");
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    bot.sendResponse(chatId, "Товар з ID " + goodsId + " не знайдено.");
                }

                sendBackButton(chatId, bot);
            } catch (NumberFormatException e) {
                bot.sendResponse(chatId, "Будь ласка, введіть валідний ID оголошення.");
                sendBackButton(chatId, bot);
            } catch (RuntimeException e) {
                bot.sendResponse(chatId, e.getMessage());
                sendBackButton(chatId, bot);
            }
        } else {
            bot.sendResponse(chatId, "Введіть ID оголошення для пошуку:");
            bot.setSearchContext("searchById");
        }
    }

    @Override
    public boolean canHandle(Update update, TelegramBot bot) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            if ("Пошук за ID".equals(messageText)) {
                return true;
            }
        }
        String searchContext = bot.getSearchContext();
        return "searchById".equals(searchContext);
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

    private void sendBackButton(long chatId, TelegramBot bot) {
        List<List<String>> options = Collections.singletonList(
                Collections.singletonList("Назад")
        );
        bot.sendReplyKeyboard(chatId, "Натисніть кнопку, щоб повернутися назад:", options);
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
