package com.hs.lab3.authservice.service;

import com.hs.lab3.authservice.dto.JwtResponse;
import com.hs.lab3.authservice.exceptions.AuthException;
import com.hs.lab3.authservice.exceptions.RegisterException;
import com.hs.lab3.authservice.jwt.JwtAuthentication;
import com.hs.lab3.authservice.model.AuthUser;
import com.hs.lab3.authservice.model.Role;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthUserService authUserService;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(String login, String password) {
        final AuthUser user = getUser(login);
        if (passwordEncoder.matches(password, user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshStorage.put(user.getLogin(), refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Password is wrong");
        }
    }

    public JwtResponse register(String login, String password, String email, String firstName, String lastName) {
        if (authUserService.checkExistedUser(login, email)) {
            throw new RegisterException("User with login: " + login + " already exists");
        }

        AuthUser user = new AuthUser();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setRoles(Collections.singleton(Role.VIEWER));

        authUserService.saveNewUser(user);

        final String accessToken = jwtProvider.generateAccessToken(user);
        final String refreshToken = jwtProvider.generateRefreshToken(user);
        refreshStorage.put(user.getLogin(), refreshToken);

        return new JwtResponse(accessToken, refreshToken);
    }

    public JwtResponse logout(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);

            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                refreshStorage.remove(login);
            }
        }
        return new JwtResponse(null, null);
    }

    public JwtResponse getAccessToken(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final AuthUser user = getUser(login);
                final String accessToken = jwtProvider.generateAccessToken(user);
                return new JwtResponse(accessToken, null);
            }
        }
        return new JwtResponse(null, null);
    }

    public JwtResponse refresh(String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final AuthUser user = getUser(login);
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(user.getLogin(), newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        throw new AuthException("JWT was not valid");
    }
    public void addRoleToUser(String login, Role role) {
        final AuthUser user = getUser(login);
        authUserService.addNewRole(user, role);
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    public AuthUser getUser(String login) {
        return authUserService.getByLogin(login)
                .orElseThrow(() -> new AuthException("User was not found"));
    }
}