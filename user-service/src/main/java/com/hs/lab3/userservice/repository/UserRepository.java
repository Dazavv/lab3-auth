package com.hs.lab3.userservice.repository;

import com.hs.lab3.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLogin(String login);
    Optional<User> findByLogin(String login);
    Page<User> findByLoginContainingIgnoreCase(String login, Pageable pageable);
    boolean existsByEmail(String email);
}
