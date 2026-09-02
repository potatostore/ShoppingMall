package com.shopping_mall_api.service.auth;

import com.shopping_mall_api.dto.auth.LogInRequestDTO;
import com.shopping_mall_api.dto.auth.LogInResponseDTO;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.global.exception.UnmatchedPasswordException;
import com.shopping_mall_api.global.security.JwtProvider;
import com.shopping_mall_api.repository.redis.RefreshTokenRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public LogInResponseDTO logIn(LogInRequestDTO logInRequestDTO){
        CheckConfig.npeCheck(logInRequestDTO, "logInRequestDTO");

        User user = userRepository.findByEmail(logInRequestDTO.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorCode.EMAIL_NOT_FOUND));

        if(!passwordEncoder.matches(logInRequestDTO.getLogInPassword(), user.getLogInPassword())){
            throw new UnmatchedPasswordException(ErrorCode.USER_PASSWORD_UNMATCHED);
        }

        String accessToken = jwtProvider.createAccessToken(user.getUserId(), user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId(), user.getRole());

        refreshTokenRepository.save(user.getUserId(), refreshToken, jwtProvider.getRefreshTokenExpiration());

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMillis(jwtProvider.getAccessTokenExpiration()))
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMillis(jwtProvider.getRefreshTokenExpiration()))
                .build();

        return new LogInResponseDTO(accessTokenCookie, refreshTokenCookie);
    }

    public void logOut(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        refreshTokenRepository.findByUserId(userId)
                        .orElseThrow(() -> new NotFoundException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        refreshTokenRepository.delete(userId);
    }
}
