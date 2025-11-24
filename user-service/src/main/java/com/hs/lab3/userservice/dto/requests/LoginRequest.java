package com.hs.lab3.userservice.dto.requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String login;
    private String password;
}