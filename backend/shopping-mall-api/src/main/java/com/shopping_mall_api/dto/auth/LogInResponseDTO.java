package com.shopping_mall_api.dto.auth;

import org.springframework.http.ResponseCookie;

// 로그인·재발급·로그아웃 공통: 응답에 실을 access / refresh 토큰 쿠키 한 쌍
public record LogInResponseDTO (
    ResponseCookie accessTokenCookie,
    ResponseCookie refreshTokenCookie
){}
