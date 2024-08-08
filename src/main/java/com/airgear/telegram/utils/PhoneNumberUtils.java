package com.airgear.telegram.utils;

public class PhoneNumberUtils {
    public static String normalizePhoneNumberForSearch(String phoneNumber) {
        String cleaned = phoneNumber.replaceAll("[^\\d]", "");
        return "+" + cleaned;
    }
}
