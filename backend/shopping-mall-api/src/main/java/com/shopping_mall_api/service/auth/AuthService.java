package com.shopping_mall_api.service.auth;

import com.shopping_mall_api.dto.auth.LogInRequestDTO;
import com.shopping_mall_api.dto.auth.LogInResponseDTO;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.InvalidTokenException;
import com.shopping_mall_api.global.exception.UnmatchedPasswordException;
import com.shopping_mall_api.global.security.JwtProvider;
import com.shopping_mall_api.repository.redis.RefreshTokenRepository;
import com.shopping_mall_api.repository.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    private String dummyEncodedPassword;

    @PostConstruct
    void initDummyPassword(){
        this.dummyEncodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    @Transactional
    public LogInResponseDTO logIn(LogInRequestDTO logInRequestDTO){
        CheckConfig.npeCheck(logInRequestDTO, "logInRequestDTO");

        // 이메일 없음 / 비밀번호 불일치를 같은 401 메시지로 응답 → 응답으로 가입 여부를 알 수 없게 함
        // 이메일이 없을 때도 BCrypt 비교를 한 번 수행해 응답 시간 차이로 가입 여부가 드러나지 않게 함
        User user = userRepository.findByEmail(logInRequestDTO.getEmail()).orElse(null);
        String encodedPassword = (user != null) ? user.getLogInPassword() : dummyEncodedPassword;

        boolean matched = passwordEncoder.matches(logInRequestDTO.getLogInPassword(), encodedPassword);

        if(user == null || !matched){
            throw new UnmatchedPasswordException(ErrorCode.LOGIN_FAILED);
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

        // 서명·만료는 정상인데 저장된 값과 다름 = 이미 교체된 토큰이거나, 다른 기기 로그인으로 덮어써진 토큰
        // 저장된 토큰까지 지우는 재사용 감지(세션 강제 종료)는 사용자당 토큰이 1개(refresh-token:{userId})인 지금 구조에선
        // 다른 기기의 정상 세션까지 끊으므로 적용하지 않음 → 멀티 디바이스 정책(Phase 5) 결정 후 도입
        if(!storedRefreshToken.equals(refreshToken)){
            throw new InvalidTokenException(ErrorCode.INVALID_REFRESH_TOKEN);
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
