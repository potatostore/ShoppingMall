package com.shopping_mall_api.global.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.util.Arrays;

// 요청에서 access token을 꺼내는 규칙 한 곳: accessToken 쿠키 우선, 없으면 Authorization: Bearer 헤더
public final class TokenResolver {
    private static final String BEARER_PREFIX = "Bearer ";

    private TokenResolver(){
    }

    public static String resolveAccessToken(HttpServletRequest request){
        if(request == null){
            return null;
        }

        if(request.getCookies() != null){
            String cookieToken = Arrays.stream(request.getCookies())
                    .filter(c -> JwtProvider.ACCESS_TOKEN_COOKIE.equals(c.getName()))
                    .findFirst()
                    .map(Cookie::getValue)
                    .orElse(null);

            if(StringUtils.hasText(cookieToken)){
                return cookieToken;
            }
        }

        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(StringUtils.hasText(bearer) && bearer.startsWith(BEARER_PREFIX)){
            return bearer.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
