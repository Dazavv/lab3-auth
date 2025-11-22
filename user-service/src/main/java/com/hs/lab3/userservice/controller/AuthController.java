package com.hs.lab3.userservice.controller;

import com.hs.lab3.userservice.dto.requests.AddRoleToUserRequest;
import com.hs.lab3.userservice.dto.requests.LoginRequest;
import com.hs.lab3.userservice.dto.requests.RefreshJwtRequest;
import com.hs.lab3.userservice.dto.requests.RegisterRequest;
import com.hs.lab3.userservice.dto.responses.JwtResponse;
import com.hs.lab3.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<JwtResponse> login(@RequestBody LoginRequest request) {
        return authService
                .login(request.getLogin(), request.getPassword());
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<ResponseEntity<?>> register(@RequestBody RegisterRequest request) {
        return authService.register(
                request.getLogin(),
                request.getPassword(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getRole())
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());
    }

    @PostMapping("/token")
    public Mono<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        return authService.getAccessToken(request.getRefreshToken());
    }

    @PostMapping("/refresh")
    public Mono<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) {
        return authService.refresh(request.getRefreshToken());
    }

    @PostMapping("/logout")
    public Mono<JwtResponse> logout(@RequestBody RefreshJwtRequest request) {
        return authService.logout(request.getRefreshToken());
    }

    @PostMapping("/add-role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Mono<String> addRoleToUser(@RequestBody AddRoleToUserRequest request) {
        return authService.addRoleToUser(request.getLogin(), request.getRole())
                .thenReturn("User with login: " + request.getLogin() + " has new role: " + request.getRole());
    }
}
