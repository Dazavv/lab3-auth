package com.hs.lab3.eventservice.auth.jwt;

import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtUtils {
    public static JwtAuthentication generate(Claims claims) {
        JwtAuthentication auth = new JwtAuthentication();

        auth.setLogin(claims.getSubject());
        auth.setEmail(claims.get("email", String.class));

        Object idObj = claims.get("id");
        if (idObj instanceof Integer) {
            auth.setId(((Integer) idObj).longValue());
        } else if (idObj instanceof Long) {
            auth.setId((Long) idObj);
        }

        List<String> rolesRaw = claims.get("roles", List.class);
        if (rolesRaw == null) {
            auth.setRoles(Collections.emptySet());
        } else {
            Set<Role> roles = rolesRaw.stream()
                    .filter(r -> r != null)
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(Role::valueOf)
                    .collect(Collectors.toSet());
            auth.setRoles(roles);
        }

        return auth;
    }
}