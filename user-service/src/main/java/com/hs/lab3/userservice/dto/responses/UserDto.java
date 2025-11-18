package com.hs.lab3.userservice.dto.responses;

import java.util.List;

public record UserDto(
        Long id,
        String username,
        String name,
        String surname,
        List<String> eventNames
) {}
