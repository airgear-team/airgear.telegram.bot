package com.airgear.telegram.service;

import com.airgear.model.Role;
import com.airgear.model.User;
import com.airgear.model.UserStatus;
import com.airgear.telegram.repository.UserRepository;
import com.airgear.telegram.utils.PhoneNumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

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
        user.setPhone(PhoneNumberUtils.normalizePhoneNumberForSearch(phone));
        user.setName(userName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRoles(createRoles());
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(OffsetDateTime.now());
        return userRepository.save(user);
    }
    private Set<Role> createRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        return roles;
    }
}
