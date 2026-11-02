package com.shopping_mall_api.global.security.filter;


import com.shopping_mall_api.global.security.JwtProvider;
import com.shopping_mall_api.global.security.TokenResolver;
import com.shopping_mall_api.repository.redis.AccessTokenBlacklistRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        String token = TokenResolver.resolveAccessToken(request);

        // refresh 토큰은 인증에 사용하지 않음 (재발급 API에서만 사용)
        // 로그아웃으로 블랙리스트에 오른 access token도 인증하지 않음
        if (StringUtils.hasText(token)
                && jwtProvider.checkValidationToken(token)
                && !jwtProvider.isRefreshToken(token)
                && !isBlacklisted(token)) {
            Long userId = jwtProvider.getUserIdFromToken(token);
            String role = jwtProvider.getRoleFromToken(token);

            List<SimpleGrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    // jti가 없는 토큰(블랙리스트 도입 전 발급)은 확인할 수 없으므로 통과, 만료되면 자연히 무효
    // Redis 장애 시에는 인증하지 않음(fail-closed): 로그아웃한 토큰이 통과하는 것보다 401이 안전
    private boolean isBlacklisted(String token){
        String jti = jwtProvider.getJtiFromToken(token);

        if(!StringUtils.hasText(jti)){
            return false;
        }

        try {
            return accessTokenBlacklistRepository.contains(jti);
        } catch (RuntimeException e) {
            log.error("Access token blacklist check failed, treat request as unauthenticated", e);
            return true;
        }
    }
}
