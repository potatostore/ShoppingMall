package com.shopping_mall_api.security;

import com.shopping_mall_api.global.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {
    // 테스트 전용 키: 사람이 읽을 수 있는 문자열로 둬서 시크릿 스캐너가 실제 키로 오인하지 않게 함
    // JwtProvider가 Base64로 디코딩하므로 영문·숫자 44자(4의 배수, 디코딩 33바이트 ≥ HS256 최소 32바이트)
    private static final String KEY = "testOnlyJwtSecretKeyForUnitTestsNotForProd00";
    private static final String OTHER_KEY = "otherJwtSecretKeyForForgedTokenTestsOnly0000";

    private JwtProvider jwtProvider;

    private static JwtProvider provider(String key, long accessMs){
        JwtProvider provider = new JwtProvider();
        ReflectionTestUtils.setField(provider, "jwtSecretKey", key);
        ReflectionTestUtils.setField(provider, "accessTokenExpiration", accessMs);
        ReflectionTestUtils.setField(provider, "refreshTokenExpiration", 60_000L);
        provider.init();
        return provider;
    }

    @BeforeEach
    void setUp(){
        jwtProvider = provider(KEY, 60_000L);
    }

    @Test
    void validToken(){
        String token = jwtProvider.createAccessToken(1L, "USER");

        assertThat(jwtProvider.checkValidationToken(token)).isTrue();
        assertThat(jwtProvider.getUserIdFromToken(token)).isEqualTo(1L);
        assertThat(jwtProvider.getRoleFromToken(token)).isEqualTo("USER");
    }

    @Test
    void tokenSignedWithOtherKey_isInvalidNotException(){
        String forged = provider(OTHER_KEY, 60_000L).createAccessToken(1L, "ADMIN");

        assertThat(jwtProvider.checkValidationToken(forged)).isFalse();
    }

    @Test
    void malformedToken_isInvalid(){
        assertThat(jwtProvider.checkValidationToken("not-a-jwt")).isFalse();
    }

    @Test
    void expiredToken_isInvalid(){
        String expired = provider(KEY, -1_000L).createAccessToken(1L, "USER");

        assertThat(jwtProvider.checkValidationToken(expired)).isFalse();
    }

    @Test
    void tokenTypeClaim(){
        String access = jwtProvider.createAccessToken(1L, "USER");
        String refresh = jwtProvider.createRefreshToken(1L, "USER");

        assertThat(jwtProvider.isRefreshToken(access)).isFalse();
        assertThat(jwtProvider.isRefreshToken(refresh)).isTrue();
    }

    @Test
    void refreshTokensAreUnique(){
        // 같은 밀리초에 연속 발급해도 jti 덕분에 달라야 rotation 비교가 의미 있음
        assertThat(jwtProvider.createRefreshToken(1L, "USER"))
                .isNotEqualTo(jwtProvider.createRefreshToken(1L, "USER"));
    }

    @Test
    void accessTokenHasJtiAndRemainingTime(){
        String access = jwtProvider.createAccessToken(1L, "USER");

        assertThat(jwtProvider.getJtiFromToken(access)).isNotBlank();
        assertThat(jwtProvider.getJtiFromToken(access)).isNotEqualTo(jwtProvider.getJtiFromToken(jwtProvider.createAccessToken(1L, "USER")));
        assertThat(jwtProvider.getRemainingMillis(access)).isBetween(1L, 60_000L);
    }
}
