package com.airgear.telegram.bot;

import com.airgear.model.User;
import com.airgear.telegram.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartMessageHandler implements MessageHandler {

    @Autowired
    private UserService userService;

    @Override
    public void handle(Update update, TelegramBot bot) {
        Message message = update.getMessage();
        long chatId = message.getChatId();

        if (message.hasContact()) {
            handleContact(message.getContact(), chatId, bot);
        } else if (message.hasText()) {
            String text = message.getText();
            if (text.startsWith("/start")) {
                handleStartCommand(chatId, bot);
            } else {
                handleTextMessage(text, chatId, bot);
            }
        }
    }

    @Override
    public boolean canHandle(Update update, TelegramBot bot) {
        Message message = update.getMessage();
        return message != null && (message.hasContact() || (message.hasText() && (message.getText().startsWith("/start") || bot.getUserSession(message.getChatId()) != null)));
    }

    private void handleStartCommand(long chatId, TelegramBot bot) {
        bot.addUserSession(chatId, "new_session");
        bot.sendResponse(chatId, "Привіт! Щоб продовжити, будь ласка, поділіться своїм номером телефону:");
        bot.sendContactRequest(chatId);
    }

    private void handleContact(Contact contact, long chatId, TelegramBot bot) {
        String phoneNumber = contact.getPhoneNumber();
        String userName = contact.getFirstName() + " " + contact.getLastName();

        User user = userService.findByPhoneNumber(phoneNumber);
        if (user == null) {
            bot.addUserSession(chatId, "awaiting_name|" + phoneNumber + "|" + userName);
            bot.sendResponse(chatId, "Користувача не знайдено. Будь ласка, введіть своє ім'я:");
        } else {
            bot.addUserSession(chatId, "authenticated|" + phoneNumber);
            bot.sendResponse(chatId, "Авторизація успішна! Тепер ви можете користуватися ботом.");
            bot.sendOptionsMenu(chatId);
        }
    }

    private void handleTextMessage(String text, long chatId, TelegramBot bot) {
        String sessionData = bot.getUserSession(chatId);

        if (sessionData == null) {
            bot.sendResponse(chatId, "Сесія не знайдена. Будь ласка, спробуйте ще раз.");
            return;
        }

        if (sessionData.startsWith("awaiting_name|")) {
            String[] parts = sessionData.split("\\|");
            String phoneNumber = parts[1];
            bot.addUserSession(chatId, "awaiting_email|" + phoneNumber + "|" + text);
            bot.sendResponse(chatId, "Будь ласка, введіть свій email:");

        } else if (sessionData.startsWith("awaiting_email|")) {
            String[] parts = sessionData.split("\\|");
            String phoneNumber = parts[1];
            String userName = parts[2];
            bot.addUserSession(chatId, "awaiting_password|" + phoneNumber + "|" + userName + "|" + text);
            bot.sendResponse(chatId, "Будь ласка, введіть свій пароль:");

        } else if (sessionData.startsWith("awaiting_password|")) {
            String[] parts = sessionData.split("\\|");
            String phoneNumber = parts[1];
            String userName = parts[2];
            String email = parts[3];
            userService.registerUser(phoneNumber, userName, email, text);
            bot.addUserSession(chatId, "authenticated|" + phoneNumber);
            bot.sendResponse(chatId, "Реєстрація успішна! Тепер ви можете користуватися ботом.");
            bot.sendOptionsMenu(chatId);

        } else if (sessionData.startsWith("authenticated|")) {
            bot.sendResponse(chatId, "Ви вже авторизовані. Якщо потрібно щось інше, напишіть нам.");

        } else {
            bot.sendResponse(chatId, "Невідомий стан сесії.");
        }
    }
}
