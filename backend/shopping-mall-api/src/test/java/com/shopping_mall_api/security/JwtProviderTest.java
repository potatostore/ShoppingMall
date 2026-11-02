package com.shopping_mall_api.security;

import com.shopping_mall_api.global.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {
    private static final String KEY = "QfbFjwmdtdTkZjue1fJZkKjTXPJz2TkvPzZHvbAmsyk=";
    private static final String OTHER_KEY = "bXlTZWNvbmRTZWNyZXRLZXlGb3JUZXN0aW5nT25seTEyMzQ=";

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
}
