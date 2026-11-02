package com.shopping_mall_api.global.security;

import com.shopping_mall_api.global.config.CheckConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtProvider {
    public static final String ACCESS_TOKEN_COOKIE = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    // access / refresh 토큰은 같은 키로 서명되므로 type 클레임으로 용도를 구분
    // (refresh 토큰을 access 토큰처럼 쓰거나, access 토큰으로 재발급받는 것을 막음)
    private static final String TOKEN_TYPE_CLAIM = "type";
    private static final String ACCESS_TYPE = "access";
    private static final String REFRESH_TYPE = "refresh";

    @Value("${jwt.secret-key}")
    private String jwtSecretKey;

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpiration;

    private Key key;

    @PostConstruct
    public void init(){
        byte[] byteKey = Decoders.BASE64.decode(jwtSecretKey);
        this.key = Keys.hmacShaKeyFor(byteKey);
    }

    public String createAccessToken(Long userId, String role){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeAndBlankCheck(role, "role");

        Date curDate = new Date();
        Date expirationDate = new Date(curDate.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .claim(TOKEN_TYPE_CLAIM, ACCESS_TYPE)
                // 로그아웃 시 블랙리스트 키로 사용
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(curDate)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long userId, String role){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeAndBlankCheck(role, "role");

        Date curDate = new Date();
        Date expirationDate = new Date(curDate.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .claim(TOKEN_TYPE_CLAIM, REFRESH_TYPE)
                // 같은 사용자가 같은 초에 두 번 재발급받아도 토큰 문자열이 달라지도록 고유 ID 부여 (rotation 비교용)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(curDate)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaims(String token){
        CheckConfig.npeAndBlankCheck(token, "token");

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromToken(String token){
        Claims claims = parseClaims(token);

        return Long.valueOf(claims.getSubject());
    }

    public String getRoleFromToken(String token){
        Claims claims = parseClaims(token);

        return claims.get("role", String.class);
    }

    public String getJtiFromToken(String token){
        return parseClaims(token).getId();
    }

    // 토큰 만료까지 남은 시간(ms). 이미 만료됐으면 0 이하
    public long getRemainingMillis(String token){
        return parseClaims(token).getExpiration().getTime() - System.currentTimeMillis();
    }

    public boolean isRefreshToken(String token){
        return REFRESH_TYPE.equals(parseClaims(token).get(TOKEN_TYPE_CLAIM, String.class));
    }

    public boolean checkValidationToken(String token){
        CheckConfig.npeAndBlankCheck(token, "token");

        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch(ExpiredJwtException e){
            log.info("JWT has been expired");
        } catch(io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            // 기존 SecurityException은 import io.jsonwebtoken.* 상황에서 java.lang.SecurityException으로 해석되어
            // 서명 위조 토큰(io.jsonwebtoken.security.SignatureException)을 잡지 못하고 필터에서 500이 발생했음
            log.info("Wrong JWT Signature");
        } catch(UnsupportedJwtException e){
            log.info("Unsupported JWT");
        } catch(JwtException | IllegalArgumentException e){
            log.info("Invalid JWT");
        }
        return false;
    }

    public long getAccessTokenExpiration(){
        return this.accessTokenExpiration;
    }

    public long getRefreshTokenExpiration(){
        return this.refreshTokenExpiration;
    }
}
