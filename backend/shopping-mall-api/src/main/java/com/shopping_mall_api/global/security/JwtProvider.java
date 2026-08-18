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

@Slf4j
@Component
public class JwtProvider {
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

    public boolean checkValidationToken(String token){
        CheckConfig.npeAndBlankCheck(token, "token");

        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch(SecurityException | MalformedJwtException e){
            log.info("Wrong JWT Signature");
        } catch(ExpiredJwtException e){
            log.info("JWT has been expired");
        } catch(UnsupportedJwtException e){
            log.info("Unsupported JWT");
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
