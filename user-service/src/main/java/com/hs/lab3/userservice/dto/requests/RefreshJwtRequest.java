package com.hs.lab3.userservice.dto.requests;

import lombok.Data;

@Data
public class RefreshJwtRequest {
    public String refreshToken;
}
