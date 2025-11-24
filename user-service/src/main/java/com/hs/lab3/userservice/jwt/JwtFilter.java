package com.hs.lab3.userservice.jwt;

import com.hs.lab3.userservice.enums.Role;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {
    private final JwtProvider jwtProvider;

    @Value("${service.token}")
    private String serviceToken;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = getTokenFromRequest(exchange);
        if (!StringUtils.hasText(token)) {
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
            log.debug("JWT invalid/expired for request {}", exchange.getRequest().getPath());
            return chain.filter(exchange);
        }

        return Mono.just(token)
                .filter(jwtProvider::validateAccessToken)
                .flatMap(t -> {
                    Claims claims = jwtProvider.getAccessClaims(t);
                    JwtAuthentication auth = JwtUtils.generate(claims);
                    auth.setAuthenticated(true);
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    private String getTokenFromRequest(ServerWebExchange exchange) {
        String bearer = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
