package com.airgear.telegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication( scanBasePackages = "com.airgear")
public class AirGearTelegramApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirGearTelegramApplication.class, args);
    }

}
