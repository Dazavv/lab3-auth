package com.hs.lab3.authservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    private String login;
    private String password;
}