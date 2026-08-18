package com.shopping_mall_api.dto.auth;

import org.springframework.http.ResponseCookie;

public record LogInResponseDTO (
    ResponseCookie accessTokenCookie,
    ResponseCookie refreshTokenCookie
){}
