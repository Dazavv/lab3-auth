package com.hs.lab3.userservice.service;

import com.hs.lab3.userservice.entity.User;
import com.hs.lab3.userservice.exceptions.UserNotFoundException;
import com.hs.lab3.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Mono<Page<User>> getAllUsers(Pageable pageable) {
        return Mono.fromCallable(() -> userRepository.findAll(pageable))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<User> getUserById(Long id) {
        return Mono.fromCallable(() -> userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException("User with id = " + id + " not found")))

                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<User> getUserByUsername(String login) {
        return Mono.fromCallable(() -> userRepository.findByLogin(login)
                        .orElseThrow(() -> new UserNotFoundException("User with login = " + login + " not found")))

                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteUserById(Long id) {
        return Mono.fromCallable(() -> {
                    if (!userRepository.existsById(id)) {
                        throw new UserNotFoundException("User with id = " + id + " not found");
                    }
                    userRepository.deleteById(id);
                    return true;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }



    public Mono<Page<User>> searchByUsername(String query, Pageable pageable) {
        return Mono.fromCallable(() ->
                        userRepository.findByLoginContainingIgnoreCase(query, pageable)
                )
                .subscribeOn(Schedulers.boundedElastic());
    }
}
