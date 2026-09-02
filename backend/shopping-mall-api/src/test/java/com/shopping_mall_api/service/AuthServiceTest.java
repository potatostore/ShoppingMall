package com.shopping_mall_api.service;

import com.shopping_mall_api.dto.auth.LogInRequestDTO;
import com.shopping_mall_api.dto.auth.LogInResponseDTO;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.global.exception.UnmatchedPasswordException;
import com.shopping_mall_api.global.security.JwtProvider;
import com.shopping_mall_api.repository.redis.RefreshTokenRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import com.shopping_mall_api.service.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtProvider jwtProvider;

    @InjectMocks private AuthService authService;

    @Test
    void logIn_successTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        ReflectionTestUtils.setField(existUser, "userId", 1L);

        LogInRequestDTO logInRequestDTO = new LogInRequestDTO(
            "qwer1234@gmail.com",
                "logInPassword"
        );

        long tokenExpiration = 20260902L;

        when(userRepository.findByEmail("qwer1234@gmail.com")).thenReturn(Optional.of(existUser));
        when(passwordEncoder.matches("logInPassword", "encodedPassword")).thenReturn(true);
        when(jwtProvider.getAccessTokenExpiration()).thenReturn(tokenExpiration);
        when(jwtProvider.getRefreshTokenExpiration()).thenReturn(tokenExpiration);
        when(jwtProvider.createRefreshToken(1L, "USER")).thenReturn("testRefreshToken");
        when(jwtProvider.createAccessToken(1L, "USER")).thenReturn("testAccessToken");

        LogInResponseDTO result = authService.logIn(logInRequestDTO);

        ResponseCookie accessResult = result.accessTokenCookie();
        ResponseCookie refreshResult = result.refreshTokenCookie();

        assertThat(accessResult.getName()).isEqualTo("accessToken");
        assertThat(accessResult.getMaxAge()).isEqualTo(Duration.ofMillis(jwtProvider.getAccessTokenExpiration()));
        assertThat(accessResult.getSameSite()).isEqualTo("Lax");
        assertThat(accessResult.getValue()).isEqualTo("testAccessToken");

        assertThat(refreshResult.getName()).isEqualTo("refreshToken");
        assertThat(refreshResult.getMaxAge()).isEqualTo(Duration.ofMillis(jwtProvider.getRefreshTokenExpiration()));
        assertThat(refreshResult.getSameSite()).isEqualTo("Lax");
        assertThat(refreshResult.getValue()).isEqualTo("testRefreshToken");

    verify(refreshTokenRepository).save(1L, "testRefreshToken", tokenExpiration);
    }

    @Test
    void logIn_userNotFoundExceptionTest(){
        LogInRequestDTO logInRequestDTO = new LogInRequestDTO(
                "1111@gmail.com",
                "encodedPassword"
        );

        when(userRepository.findByEmail("1111@gmail.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.logIn(logInRequestDTO));
    }

    @Test
    void logIn_passwordUnmatchedExceptionTest(){
        User existUser = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();

        LogInRequestDTO logInRequestDTO = new LogInRequestDTO(
                "qwer1234@gmail.com",
                "encodedPasswordUnmatchedTest"
        );

        when(userRepository.findByEmail("qwer1234@gmail.com")).thenReturn(Optional.of(existUser));

        assertThrows(UnmatchedPasswordException.class, () -> authService.logIn(logInRequestDTO));
    }

    @Test
    void logOut_successTest(){
        when(refreshTokenRepository.findByUserId(1L)).thenReturn(Optional.of("NotEmpty"));

        authService.logOut(1L);

        verify(refreshTokenRepository).delete(1L);
    }

    @Test
    void logOut_userNotFoundExceptionTest(){
        when(refreshTokenRepository.findByUserId(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.logOut(999L));
    }
}
