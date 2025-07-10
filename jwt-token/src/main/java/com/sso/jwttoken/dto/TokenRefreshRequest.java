package com.sso.jwttoken.dto;

import lombok.Data;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
}
