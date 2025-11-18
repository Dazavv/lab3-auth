package com.hs.lab3.authservice.dto;

import com.hs.lab3.authservice.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddRoleToUserRequest {
    private String login;
    private Role role;
}
