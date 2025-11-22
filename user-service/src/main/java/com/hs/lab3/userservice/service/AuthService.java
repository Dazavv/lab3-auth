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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUserService authUserService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, String> refreshStorage = new HashMap<>();

    public Mono<JwtResponse> login(String login, String password) {
        return getUser(login)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(user -> {
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.fromSupplier(() -> {
                            String access = jwtProvider.generateAccessToken(user);
                            String refresh = jwtProvider.generateRefreshToken(user);
                            refreshStorage.put(user.getLogin(), refresh);
                            return new JwtResponse(access, refresh);
                        });
                    } else {
                        return Mono.error(new WrongCredentialsException("Password is wrong"));
                    }
                });
    }

    public Mono<User> register(String login, String password,
                               String email, String firstName, String lastName, Role role) {

        return Mono.fromCallable(() -> authUserService.checkExistedUser(login, email))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new WrongCredentialsException(
                                "User with login: " + login + " already exists"));
                    }

                    User user = new User();
                    user.setLogin(login);
                    user.setPassword(passwordEncoder.encode(password));
                    user.setName(firstName);
                    user.setSurname(lastName);
                    user.setEmail(email);
                    user.setRoles(Collections.singleton(role));

                    return Mono.fromCallable(() -> authUserService.saveNewUser(user))
                            .subscribeOn(Schedulers.boundedElastic());
                });
    }

    public Mono<JwtResponse> logout(String refreshToken) {
        return Mono.fromSupplier(() -> {
            if (jwtProvider.validateRefreshToken(refreshToken)) {
                Claims claims = jwtProvider.getRefreshClaims(refreshToken);
                String login = claims.getSubject();

                String stored = refreshStorage.get(login);
                if (stored != null && stored.equals(refreshToken)) {
                    refreshStorage.remove(login);
                }
            }
            return new JwtResponse(null, null);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<JwtResponse> getAccessToken(String refreshToken) {
        return Mono.fromSupplier(() -> {
            if (!jwtProvider.validateRefreshToken(refreshToken))
                return new JwtResponse(null, null);

            Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            String login = claims.getSubject();
            String saved = refreshStorage.get(login);

            if (saved != null && saved.equals(refreshToken)) {
                User user = getUserBlocking(login);
                String access = jwtProvider.generateAccessToken(user);
                return new JwtResponse(access, null);
            }

            return new JwtResponse(null, null);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<JwtResponse> refresh(String refreshToken) {
        return Mono.fromCallable(() -> jwtProvider.validateRefreshToken(refreshToken))
                .flatMap(valid -> {
                    if (!valid)
                        return Mono.error(new JWTNotValidException("JWT was not valid"));

                    return Mono.fromCallable(() -> {
                        Claims claims = jwtProvider.getRefreshClaims(refreshToken);
                        String login = claims.getSubject();
                        String saved = refreshStorage.get(login);

                        if (saved != null && saved.equals(refreshToken)) {
                            User user = getUserBlocking(login);
                            String access = jwtProvider.generateAccessToken(user);
                            String newRefresh = jwtProvider.generateRefreshToken(user);

                            refreshStorage.put(login, newRefresh);

                            return new JwtResponse(access, newRefresh);
                        }

                        throw new JWTNotValidException("JWT was not valid");
                    }).subscribeOn(Schedulers.boundedElastic());
                });
    }

    public Mono<Void> addRoleToUser(String login, Role role) {
        return getUser(login)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(user -> Mono.fromRunnable(() -> authUserService.addNewRole(user, role))
                        .subscribeOn(Schedulers.boundedElastic()))
                .then();
    }

    public Mono<JwtAuthentication> getAuthInfo() {
        return Mono.fromCallable(() ->
                (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication()
        );
    }

    private Mono<User> getUser(String login) {
        return Mono.fromCallable(() ->
                authUserService.getByLogin(login)
                        .orElseThrow(() -> new UserNotFoundException("User with login " + login + " was not found"))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    private User getUserBlocking(String login) {
        return authUserService.getByLogin(login)
                .orElseThrow(() -> new UserNotFoundException("User with login " + login + " was not found"));
    }
}