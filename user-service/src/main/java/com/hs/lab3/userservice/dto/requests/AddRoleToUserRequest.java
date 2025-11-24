package com.hs.lab3.userservice.dto.requests;

import com.hs.lab3.userservice.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddRoleToUserRequest {
    private String login;
    private Role role;
}
