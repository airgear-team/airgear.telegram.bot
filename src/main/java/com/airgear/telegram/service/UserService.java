package com.airgear.telegram.service;

import com.airgear.model.User;
import com.airgear.telegram.repository.UserRepository;
import com.airgear.telegram.utils.PhoneNumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findByPhoneNumber(String phoneNumber) {
        String normalizedPhone = PhoneNumberUtils.normalizePhoneNumberForSearch(phoneNumber);
        return userRepository.findByPhone(normalizedPhone).orElse(null);
    }

    public User registerUser(String phone, String userName, String email, String password) {
        User user = new User();
        user.setPhone(phone);
        user.setName(userName);
        user.setEmail(email);
        user.setPassword(password); // захешувати пароль не забути)
        return userRepository.save(user);
    }
}
