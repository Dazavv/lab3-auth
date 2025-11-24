package com.hs.lab3.userservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
    USER("USER"),
    ADMIN("ADMIN"),
    REDACTOR("REDACTOR"),
    LEAD("LEAD"),
    SERVICE("SERVICE");

    private final String value;

    @Override
    public String getAuthority() {
        return value;
    }

    @JsonCreator
    public static Role from(String role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        return Role.valueOf(role.toUpperCase());
    }
}