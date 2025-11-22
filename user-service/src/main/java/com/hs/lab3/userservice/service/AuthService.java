package com.hs.lab3.userservice.service;

import com.hs.lab3.userservice.dto.responses.JwtResponse;
import com.hs.lab3.userservice.entity.User;
import com.hs.lab3.userservice.enums.Role;
import com.hs.lab3.userservice.exceptions.JWTNotValidException;
import com.hs.lab3.userservice.exceptions.UserNotFoundException;
import com.hs.lab3.userservice.exceptions.WrongCredentialsException;
import com.hs.lab3.userservice.jwt.JwtAuthentication;
import com.hs.lab3.userservice.jwt.JwtProvider;
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
        final User user = getUser(login);
        if (passwordEncoder.matches(password, user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshStorage.put(user.getLogin(), refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new WrongCredentialsException("Password is wrong");
        }
    }

    public JwtResponse register(String login, String password, String email, String firstName, String lastName) {
        if (authUserService.checkExistedUser(login, email)) {
            throw new WrongCredentialsException("User with login: \"" + login + "\" already exists");
        }

        User user = new User();
        user.setLogin(login);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(firstName);
        user.setSurname(lastName);
        user.setEmail(email);
        user.setRoles(Collections.singleton(Role.USER));

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
                final User user = getUser(login);
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
                final User user = getUser(login);
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(user.getLogin(), newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        throw new JWTNotValidException( "JWT was not valid");
    }
    public void addRoleToUser(String login, Role role) {
        final User user = getUser(login);
        authUserService.addNewRole(user, role);
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    public User getUser(String login) {
        return authUserService.getByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("User with login - " + login + " was not found"));
    }
}