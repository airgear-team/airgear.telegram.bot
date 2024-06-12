package com.airgear.telegram.bot;

import com.airgear.telegram.dto.GoodsResponse;
import org.telegram.telegrambots.meta.api.objects.Update;

public class GoodsMessageHandler implements MessageHandler{
    @Override
    public void handle(Update update, TelegramBot bot) {
        long chatId = update.getMessage().getChatId();
        try {
            Long goodsId = Long.parseLong(update.getMessage().getText());
            GoodsResponse goodsResponseDTO = bot.getGoodsService().getGoodsById(goodsId);
            bot.sendResponse(chatId, goodsResponseDTO.toString());
        } catch (NumberFormatException e) {
            bot.sendResponse(chatId, "Будь ласка, введіть валідний ID оголошення.");
        } catch (RuntimeException e) {
            bot.sendResponse(chatId, e.getMessage());
        }
    }

    @Override
    public boolean canHandle(Update update) {
        return !update.getMessage().getText().startsWith("/start");
    }
}

