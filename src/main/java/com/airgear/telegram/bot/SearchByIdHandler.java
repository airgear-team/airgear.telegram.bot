package com.airgear.telegram.bot;

import com.airgear.telegram.dto.GoodsResponse;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.Collections;

@Component
public class SearchByIdHandler implements MessageHandler {

    @Override
    public void handle(Update update, TelegramBot bot) {
        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        if (messageText.equals("Назад")) {
            sendMainMenu(chatId, bot);
            return;
        }

        if (bot.getSearchContext().equals("searchById")) {
            try {
                Long goodsId = Long.parseLong(messageText);
                GoodsResponse goodsResponseDTO = bot.getGoodsService().getGoodsById(goodsId);
                bot.sendResponse(chatId, goodsResponseDTO.toFormattedString());
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
        return update.getMessage().getText().equals("Пошук за ID") || bot.getSearchContext().equals("searchById");
    }

    private void sendMainMenu(long chatId, TelegramBot bot) {
        bot.sendReplyKeyboard(chatId, "Оберіть опцію для пошуку:", Arrays.asList("Пошук за ID", "Пошук за словами"));
        bot.setSearchContext("");
    }

    private void sendBackButton(long chatId, TelegramBot bot) {
        bot.sendReplyKeyboard(chatId, "Натисніть кнопку, щоб повернутися назад:", Collections.singletonList("Назад"));
    }
}
