package com.hs.lab3.authservice.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
    VIEWER("VIEWER"),
    ADMIN("ADMIN"),
    REDACTOR("REDACTOR"),
    LEAD("LEAD");

    private final String value;

    @Override
    public String getAuthority() {
        return value;
    }
}