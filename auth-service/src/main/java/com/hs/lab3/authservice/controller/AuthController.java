package com.hs.lab3.authservice.controller;

import com.hs.lab3.authservice.dto.*;
import com.hs.lab3.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        final JwtResponse token = authService.login(request.getLogin(), request.getPassword());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterRequest request) {
        final JwtResponse token = authService.register(
                request.getLogin(),
                request.getPassword(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName());
        return ResponseEntity.ok(token);
    }

    @PostMapping("token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<JwtResponse> logout(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/add-role")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> addRoleToUser(@RequestBody AddRoleToUserRequest request) {
        authService.addRoleToUser(request.getLogin(), request.getRole());
        return ResponseEntity.ok("User with login: " + request.getLogin() + " has new role: " + request.getRole());
    }


}
