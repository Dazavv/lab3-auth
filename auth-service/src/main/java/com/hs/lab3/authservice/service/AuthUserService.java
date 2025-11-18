package com.hs.lab3.authservice.service;

import com.hs.lab3.authservice.model.AuthUser;
import com.hs.lab3.authservice.model.Role;
import com.hs.lab3.authservice.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthUserService {
    private final AuthRepository authRepository;

    public Optional<AuthUser> getByLogin(String login) {
        return authRepository.findByLogin(login);
    }

    public boolean checkExistedUser(String login, String email) {
        return authRepository.existsByLogin(login) || authRepository.existsByEmail(email);
    }

    public void saveNewUser(AuthUser user) {
        authRepository.save(user);
    }

    public void addNewRole(AuthUser user, Role role) {
        user.getRoles().add(role);
        authRepository.save(user);
    }
}
