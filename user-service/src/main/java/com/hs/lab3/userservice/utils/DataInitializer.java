package com.hs.lab3.userservice.utils;

import com.hs.lab3.userservice.entity.User;
import com.hs.lab3.userservice.enums.Role;
import com.hs.lab3.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.login:admin}")
    private String adminLogin;

    @Value("${admin.password:admin}")
    private String adminPassword;

    @Value("${admin.name:admin}")
    private String adminName;

    @Value("${admin.surname:admin}")
    private String adminSurname;

    @Value("${admin.email:admin}")
    private String adminEmail;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByLogin(adminLogin).isEmpty()) {
            User admin = new User();
            admin.setLogin(adminLogin);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setName(adminName);
            admin.setSurname(adminSurname);
            admin.setEmail(adminEmail);
            admin.setRoles(Collections.singleton(Role.ADMIN));
            userRepository.save(admin);
            System.out.println("Admin user created");
        }
    }
}
