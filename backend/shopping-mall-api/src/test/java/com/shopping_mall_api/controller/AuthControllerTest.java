package com.shopping_mall_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping_mall_api.dto.auth.LogInRequestDTO;
import com.shopping_mall_api.dto.auth.LogInResponseDTO;
import com.shopping_mall_api.global.constant.ApiURLNames;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.InvalidTokenException;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.global.exception.UnmatchedPasswordException;
import com.shopping_mall_api.service.auth.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AuthService authService;

    @Test
    void logIn_successTest() throws Exception{
        String accessToken = "authControllerTestAccessToken";
        String refreshToken = "authControllerTestRefreshToken";

        long accessTokenExpiration = 2026090301L;
        long refreshTokenExpiration = 2026090302L;

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMillis(accessTokenExpiration))
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMillis(refreshTokenExpiration))
                .build();

        LogInResponseDTO logInResponseDTO = new LogInResponseDTO(
            accessTokenCookie,
            refreshTokenCookie
        );

        LogInRequestDTO logInRequestDTO = new LogInRequestDTO(
                "1111@gmail.com",
                "qwer1234"
        );

        when(authService.logIn(any(LogInRequestDTO.class))).thenReturn(logInResponseDTO);

        mockMvc.perform(post(ApiURLNames.loginURL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(logInRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(cookie().value("accessToken", "authControllerTestAccessToken"))
                .andExpect(cookie().value("refreshToken", "authControllerTestRefreshToken"))
                .andExpect(jsonPath("$.message").value("Success : Log In"));
    }

    @Test
    void logIn_userNotFoundTest() throws Exception{
        LogInRequestDTO logInRequestDTO = new LogInRequestDTO(
                "1111@gmail.com",
                "qwer1234"
        );

        when(authService.logIn(any(LogInRequestDTO.class))).thenThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND));

        mockMvc.perform(post(ApiURLNames.loginURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logInRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void logIn_unmatchedPasswordTest() throws Exception{
        LogInRequestDTO logInRequestDTO = new LogInRequestDTO(
                "1111@gmail.com",
                "qwer1234"
        );

        when(authService.logIn(any(LogInRequestDTO.class))).thenThrow(new UnmatchedPasswordException(ErrorCode.USER_PASSWORD_UNMATCHED));

        mockMvc.perform(post(ApiURLNames.loginURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logInRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("User password unmatched"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void logIn_inValidRequestTest() throws Exception{
        String body = "{\"email\":\"\",\"logInPassword\":\"pw\"}";

        mockMvc.perform(post(ApiURLNames.loginURL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));

        verify(authService, never()).logIn(any());
    }

    @Test
    void logOut_successTest() throws Exception {
        Long userId = 1L;
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userId, null, List.of(new SimpleGrantedAuthority("USER"))
        );

        when(authService.logOut(userId)).thenReturn(new LogInResponseDTO(
                ResponseCookie.from("accessToken", "").path("/").maxAge(Duration.ZERO).build(),
                ResponseCookie.from("refreshToken", "").path("/").maxAge(Duration.ZERO).build()
        ));

        SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            mockMvc.perform(delete(ApiURLNames.logOutURL))
                    .andExpect(status().isOk())
                    .andExpect(content().string(""))
                    .andExpect(cookie().maxAge("accessToken", 0))
                    .andExpect(cookie().maxAge("refreshToken", 0));

            verify(authService).logOut(userId);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void reissue_successTest() throws Exception{
        when(authService.reissue("oldRefresh")).thenReturn(new LogInResponseDTO(
                ResponseCookie.from("accessToken", "newAccess").path("/").maxAge(Duration.ofMinutes(30)).build(),
                ResponseCookie.from("refreshToken", "newRefresh").path("/").maxAge(Duration.ofDays(7)).build()
        ));

        mockMvc.perform(post(ApiURLNames.reissueURL)
                        .cookie(new Cookie("refreshToken", "oldRefresh")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(cookie().value("accessToken", "newAccess"))
                .andExpect(cookie().value("refreshToken", "newRefresh"));
    }

    @Test
    void reissue_invalidTokenTest() throws Exception{
        when(authService.reissue(null)).thenThrow(new InvalidTokenException(ErrorCode.INVALID_REFRESH_TOKEN));

        mockMvc.perform(post(ApiURLNames.reissueURL))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid or expired refresh token"));
    }
}
