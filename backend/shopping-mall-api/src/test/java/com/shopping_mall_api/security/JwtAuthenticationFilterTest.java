package com.shopping_mall_api.security;

import com.shopping_mall_api.global.security.JwtProvider;
import com.shopping_mall_api.global.security.filter.JwtAuthenticationFilter;
import com.shopping_mall_api.repository.redis.AccessTokenBlacklistRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {
    private JwtProvider jwtProvider;
    private AccessTokenBlacklistRepository blacklist;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp(){
        jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "jwtSecretKey", TestJwtKeys.randomBase64Key());
        ReflectionTestUtils.setField(jwtProvider, "accessTokenExpiration", 60_000L);
        ReflectionTestUtils.setField(jwtProvider, "refreshTokenExpiration", 60_000L);
        jwtProvider.init();

        blacklist = mock(AccessTokenBlacklistRepository.class);
        filter = new JwtAuthenticationFilter(jwtProvider, blacklist);
    }

    @AfterEach
    void clear(){
        SecurityContextHolder.clearContext();
    }

    private void runWithCookie(String token) throws Exception{
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("accessToken", token));
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
    }

    @Test
    void validAccessToken_authenticates() throws Exception{
        runWithCookie(jwtProvider.createAccessToken(1L, "USER"));

        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(1L);
    }

    @Test
    void blacklistedAccessToken_notAuthenticated() throws Exception{
        when(blacklist.contains(anyString())).thenReturn(true);

        runWithCookie(jwtProvider.createAccessToken(1L, "USER"));

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void refreshToken_notUsedForAuthentication() throws Exception{
        runWithCookie(jwtProvider.createRefreshToken(1L, "USER"));

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verifyNoInteractions(blacklist);
    }

    @Test
    void redisFailure_failsClosed() throws Exception{
        when(blacklist.contains(anyString())).thenThrow(new RedisConnectionFailureException("down"));

        runWithCookie(jwtProvider.createAccessToken(1L, "USER"));

        // 예외로 500을 내지 않고, 인증 없이 통과 → 보호된 API는 401
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
