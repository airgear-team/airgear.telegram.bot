package com.airgear.telegram.config;

import com.airgear.telegram.bot.GoodsMessageHandler;
import com.airgear.telegram.bot.MessageHandler;
import com.airgear.telegram.bot.StartMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Bean
    public MessageHandler startMessageHandler() {
        return new StartMessageHandler();
    }

    @Bean
    public MessageHandler goodsMessageHandler() {
        return new GoodsMessageHandler();
    }
}
