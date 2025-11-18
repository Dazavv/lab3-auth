package com.hs.lab3.userservice.requests;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank String username,
        String name,
        String surname
) {}
