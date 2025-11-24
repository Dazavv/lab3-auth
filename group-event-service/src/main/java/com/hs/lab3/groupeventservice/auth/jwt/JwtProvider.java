package com.hs.lab3.groupeventservice.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtProvider {
    private final SecretKey jwtAccessSecretKey;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecretKey) {
        this.jwtAccessSecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecretKey));
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        try {
            Jwts.parser()
                    .verifyWith(jwtAccessSecretKey)
                    .build()
                    .parseSignedClaims(accessToken);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.warn("Token expired", expEx);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token invalid", e);
        }
        return false;
    }

    public Claims getAccessClaims(@NonNull String token) {
        return Jwts.parser()
                .verifyWith(jwtAccessSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}