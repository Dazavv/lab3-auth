package com.hs.lab3.authservice.repository;

import com.hs.lab3.authservice.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByLogin(String login);
    boolean existsByLogin(String login);
    boolean existsByEmail(String email);
}