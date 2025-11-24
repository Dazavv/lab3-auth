package com.hs.lab3.userservice.service;

import com.hs.lab3.userservice.entity.User;
import com.hs.lab3.userservice.enums.Role;
import com.hs.lab3.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthUserService {
    private final UserRepository userRepository;

    public Optional<User> getByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public boolean checkExistedUser(String login, String email) {
        return userRepository.existsByLogin(login) || userRepository.existsByEmail(email);
    }

    public User saveNewUser(User user) {
        return userRepository.save(user);
    }

    public void addNewRole(User user, Role role) {
        user.getRoles().add(role);
        userRepository.save(user);
    }
}
