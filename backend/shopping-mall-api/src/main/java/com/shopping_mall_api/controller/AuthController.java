package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.auth.LogInRequestDTO;
import com.shopping_mall_api.dto.auth.LogInResponseDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import com.shopping_mall_api.global.constant.ApiURLNames;
import com.shopping_mall_api.global.security.JwtProvider;
import com.shopping_mall_api.global.security.TokenResolver;
import jakarta.servlet.http.HttpServletRequest;
import com.shopping_mall_api.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(ApiURLNames.loginURL)
    public ResponseEntity<ApiResponse<Void>> logIn(@Valid @RequestBody LogInRequestDTO logInRequestDTO){
        LogInResponseDTO logInResponseDTO = authService.logIn(logInRequestDTO);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, logInResponseDTO.accessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, logInResponseDTO.refreshTokenCookie().toString())
                .body(ApiResponse.success(
                        "Success : Log In",
                        null
                ));
    }

    // access token이 만료돼도 호출할 수 있도록 공개 API, refresh token은 HttpOnly 쿠키에서 읽음
    @PostMapping(ApiURLNames.reissueURL)
    public ResponseEntity<ApiResponse<Void>> reissue(
            @CookieValue(name = JwtProvider.REFRESH_TOKEN_COOKIE, required = false) String refreshToken){
        LogInResponseDTO tokenCookies = authService.reissue(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenCookies.accessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, tokenCookies.refreshTokenCookie().toString())
                .body(ApiResponse.success(
                        "Success : reissue token",
                        null
                ));
    }

    // 응답 바디는 기존처럼 비우고, 두 토큰 쿠키를 Max-Age=0으로 만료시킴
    @DeleteMapping(ApiURLNames.logOutURL)
    public ResponseEntity<Void> logOut(@AuthenticationPrincipal Long userId, HttpServletRequest request){
        LogInResponseDTO expiredCookies = authService.logOut(userId, TokenResolver.resolveAccessToken(request));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expiredCookies.accessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, expiredCookies.refreshTokenCookie().toString())
                .build();
    }
}
