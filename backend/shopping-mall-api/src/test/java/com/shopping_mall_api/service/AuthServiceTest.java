package com.shopping_mall_api.service;

import com.shopping_mall_api.dto.auth.LogInRequestDTO;
import com.shopping_mall_api.dto.auth.LogInResponseDTO;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.InvalidTokenException;
import com.shopping_mall_api.global.exception.UnmatchedPasswordException;
import com.shopping_mall_api.global.security.JwtProvider;
import com.shopping_mall_api.repository.redis.AccessTokenBlacklistRepository;
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
    @Mock private AccessTokenBlacklistRepository accessTokenBlacklistRepository;
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

        UnmatchedPasswordException e = assertThrows(UnmatchedPasswordException.class, () -> authService.logIn(logInRequestDTO));
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED);
        // 이메일이 없어도 비밀번호 비교를 수행 (응답 시간으로 가입 여부 노출 방지)
        verify(passwordEncoder).matches(eq("encodedPassword"), any());
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

        UnmatchedPasswordException e = assertThrows(UnmatchedPasswordException.class, () -> authService.logIn(logInRequestDTO));
        // 이메일 없음과 같은 에러 코드·메시지
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.LOGIN_FAILED);
    }

    @Test
    void logOut_successTest(){
        LogInResponseDTO result = authService.logOut(1L, null);

        verify(refreshTokenRepository).delete(1L);
        assertThat(result.accessTokenCookie().getMaxAge()).isEqualTo(Duration.ZERO);
        assertThat(result.refreshTokenCookie().getMaxAge()).isEqualTo(Duration.ZERO);
    }

    @Test
    void logOut_blacklistsCurrentAccessTokenTest(){
        when(jwtProvider.checkValidationToken("access")).thenReturn(true);
        when(jwtProvider.getJtiFromToken("access")).thenReturn("jti-1");
        when(jwtProvider.getRemainingMillis("access")).thenReturn(120_000L);

        authService.logOut(1L, "access");

        verify(refreshTokenRepository).delete(1L);
        // 남은 만료 시간만큼만 블랙리스트에 보관
        verify(accessTokenBlacklistRepository).add("jti-1", 120_000L);
    }

    @Test
    void logOut_invalidAccessTokenSkipsBlacklistTest(){
        when(jwtProvider.checkValidationToken("expired")).thenReturn(false);

        authService.logOut(1L, "expired");

        verify(refreshTokenRepository).delete(1L);
        verifyNoInteractions(accessTokenBlacklistRepository);
    }

    private User reissueUser(){
        User user = User.builder()
                .email("qwer1234@gmail.com")
                .logInPassword("encodedPassword")
                .name("김민준")
                .role("USER")
                .phoneNumber("01011111111")
                .birthday(LocalDate.of(2026, 8, 27))
                .build();
        ReflectionTestUtils.setField(user, "userId", 1L);
        return user;
    }

    @Test
    void reissue_successRotatesRefreshTokenTest(){
        when(jwtProvider.checkValidationToken("oldRefresh")).thenReturn(true);
        when(jwtProvider.isRefreshToken("oldRefresh")).thenReturn(true);
        when(jwtProvider.getUserIdFromToken("oldRefresh")).thenReturn(1L);
        when(refreshTokenRepository.findByUserId(1L)).thenReturn(Optional.of("oldRefresh"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(reissueUser()));
        when(jwtProvider.createAccessToken(1L, "USER")).thenReturn("newAccess");
        when(jwtProvider.createRefreshToken(1L, "USER")).thenReturn("newRefresh");
        when(jwtProvider.getAccessTokenExpiration()).thenReturn(1800000L);
        when(jwtProvider.getRefreshTokenExpiration()).thenReturn(604800000L);

        LogInResponseDTO result = authService.reissue("oldRefresh");

        assertThat(result.accessTokenCookie().getValue()).isEqualTo("newAccess");
        assertThat(result.refreshTokenCookie().getValue()).isEqualTo("newRefresh");
        // rotation: Redis의 refresh token을 새 값으로 교체
        verify(refreshTokenRepository).save(1L, "newRefresh", 604800000L);
    }

    @Test
    void reissue_missingCookieTest(){
        assertThrows(InvalidTokenException.class, () -> authService.reissue(null));
        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    void reissue_invalidOrExpiredTokenTest(){
        when(jwtProvider.checkValidationToken("expired")).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> authService.reissue("expired"));
        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    void reissue_accessTokenRejectedTest(){
        when(jwtProvider.checkValidationToken("access")).thenReturn(true);
        when(jwtProvider.isRefreshToken("access")).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> authService.reissue("access"));
        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    void reissue_notStoredTest(){
        when(jwtProvider.checkValidationToken("loggedOut")).thenReturn(true);
        when(jwtProvider.isRefreshToken("loggedOut")).thenReturn(true);
        when(jwtProvider.getUserIdFromToken("loggedOut")).thenReturn(1L);
        when(refreshTokenRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> authService.reissue("loggedOut"));
    }

    @Test
    void reissue_rotatedOrOverwrittenTokenRejectedTest(){
        when(jwtProvider.checkValidationToken("usedRefresh")).thenReturn(true);
        when(jwtProvider.isRefreshToken("usedRefresh")).thenReturn(true);
        when(jwtProvider.getUserIdFromToken("usedRefresh")).thenReturn(1L);
        when(refreshTokenRepository.findByUserId(1L)).thenReturn(Optional.of("latestRefresh"));

        assertThrows(InvalidTokenException.class, () -> authService.reissue("usedRefresh"));
        // 저장된 최신 토큰(다른 기기일 수 있음)은 지우지 않고, 새 토큰도 발급하지 않음
        verify(refreshTokenRepository, never()).delete(anyLong());
        verify(refreshTokenRepository, never()).save(anyLong(), any(), anyLong());
    }
}
