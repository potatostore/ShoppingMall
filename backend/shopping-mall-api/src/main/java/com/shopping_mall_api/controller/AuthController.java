package com.shopping_mall_api.controller;

import com.shopping_mall_api.dto.auth.LogInRequestDTO;
import com.shopping_mall_api.dto.auth.LogInResponseDTO;
import com.shopping_mall_api.global.api.ApiResponse;
import com.shopping_mall_api.global.constant.ApiURLNames;
import com.shopping_mall_api.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(ApiURLNames.loginURL)
    public ResponseEntity<ApiResponse<String>> logIn(@Valid @RequestBody LogInRequestDTO logInRequestDTO){
        LogInResponseDTO logInResponseDTO = authService.logIn(logInRequestDTO);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, logInResponseDTO.accessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, logInResponseDTO.refreshTokenCookie().toString())
                .body(ApiResponse.success(
                        "Success : Log In",
                        null
                ));
    }

    @DeleteMapping(ApiURLNames.logOutURL)
    public void logOut(@AuthenticationPrincipal Long userId){
        authService.logOut(userId);
    }
}
