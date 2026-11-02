package com.shopping_mall_api.service.auth;

import com.shopping_mall_api.dto.auth.LogInRequestDTO;
import com.shopping_mall_api.dto.auth.LogInResponseDTO;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.InvalidTokenException;
import com.shopping_mall_api.global.exception.NotFoundException;
import com.shopping_mall_api.global.exception.UnmatchedPasswordException;
import com.shopping_mall_api.global.security.JwtProvider;
import com.shopping_mall_api.repository.redis.RefreshTokenRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

        return issueTokens(user);
    }

    // access token 재발급 + refresh token rotation (재발급할 때마다 refresh token도 새로 발급하고 Redis 값을 교체)
    public LogInResponseDTO reissue(String refreshToken){
        if(!StringUtils.hasText(refreshToken)
                || !jwtProvider.checkValidationToken(refreshToken)
                || !jwtProvider.isRefreshToken(refreshToken)){
            throw new InvalidTokenException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtProvider.getUserIdFromToken(refreshToken);

        String storedRefreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidTokenException(ErrorCode.INVALID_REFRESH_TOKEN));

        if(!storedRefreshToken.equals(refreshToken)){
            // 서명·만료는 정상인데 저장된 값과 다름 = 이미 교체된(사용된) refresh token의 재사용
            // 탈취 가능성이 있으므로 저장된 토큰도 지워 해당 사용자의 세션을 끊음 (재로그인 필요)
            refreshTokenRepository.delete(userId);
            throw new InvalidTokenException(ErrorCode.INVALID_REFRESH_TOKEN, "Refresh token reuse detected");
        }

        // role이 바뀌었을 수 있으므로 토큰 클레임이 아니라 DB의 현재 값으로 새 토큰 발급
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidTokenException(ErrorCode.INVALID_REFRESH_TOKEN));

        return issueTokens(user);
    }

    // refresh token이 이미 만료·삭제된 경우에도 쿠키는 지워야 하므로 예외 없이 처리 (멱등)
    public LogInResponseDTO logOut(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        refreshTokenRepository.delete(userId);

        return new LogInResponseDTO(
                expiredCookie(JwtProvider.ACCESS_TOKEN_COOKIE),
                expiredCookie(JwtProvider.REFRESH_TOKEN_COOKIE)
        );
    }

    private LogInResponseDTO issueTokens(User user){
        String accessToken = jwtProvider.createAccessToken(user.getUserId(), user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId(), user.getRole());

        refreshTokenRepository.save(user.getUserId(), refreshToken, jwtProvider.getRefreshTokenExpiration());

        return new LogInResponseDTO(
                tokenCookie(JwtProvider.ACCESS_TOKEN_COOKIE, accessToken, jwtProvider.getAccessTokenExpiration()),
                tokenCookie(JwtProvider.REFRESH_TOKEN_COOKIE, refreshToken, jwtProvider.getRefreshTokenExpiration())
        );
    }

    private ResponseCookie tokenCookie(String name, String value, long expirationMs){
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMillis(expirationMs))
                .build();
    }

    // 같은 이름·path·속성에 Max-Age=0을 보내야 브라우저가 기존 쿠키를 삭제함
    private ResponseCookie expiredCookie(String name){
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
    }
}
