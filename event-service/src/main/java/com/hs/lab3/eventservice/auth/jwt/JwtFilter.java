package com.hs.lab3.eventservice.auth.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter implements WebFilter {

    private final JwtProvider jwtProvider;

    @Value("${service.token}")
    private String serviceToken;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = extract(exchange);
        log.info("token: {}", token);

        if (token == null) {
            return chain.filter(exchange);
        }
        if (token.equals(serviceToken)) {
            log.debug("Service token accepted for path {}", exchange.getRequest().getPath());

            JwtAuthentication auth = new JwtAuthentication();
            auth.setAuthenticated(true);
            auth.setRoles(Set.of(Role.SERVICE));
            auth.setLogin("service");
            auth.setEmail("service@internal");

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
        }

        if (!jwtProvider.validateAccessToken(token)) {
            log.debug("Invalid JWT for request {}", exchange.getRequest().getPath());
            return chain.filter(exchange);
        }

        Claims claims;
        try {
            claims = jwtProvider.getAccessClaims(token);
        } catch (Exception e) {
            log.warn("Can't parse JWT claims", e);
            return chain.filter(exchange);
        }

        JwtAuthentication authentication = JwtUtils.generate(claims);
        authentication.setAuthenticated(true);

        return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }

    private String extract(ServerWebExchange exchange) {
        String h = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return h != null && h.startsWith("Bearer ") ? h.substring(7) : null;
    }
}
