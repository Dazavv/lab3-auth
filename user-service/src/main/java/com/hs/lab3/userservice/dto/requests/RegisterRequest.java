package com.hs.lab3.userservice.dto.requests;

import com.hs.lab3.userservice.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    private @NotBlank String login;
    private @NotBlank String password;
    private @NotBlank String firstName;
    private @NotBlank String lastName;
    private @NotBlank String email;
    private @NotBlank Role role;
}