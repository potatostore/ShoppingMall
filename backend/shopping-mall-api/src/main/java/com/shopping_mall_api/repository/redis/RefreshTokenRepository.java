package com.shopping_mall_api.repository.redis;

import com.shopping_mall_api.global.config.CheckConfig;
import com.shopping_mall_api.global.exception.ErrorCode;
import com.shopping_mall_api.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
    private final StringRedisTemplate stringRedisTemplate;
    private final String refreshTokenKey = "refresh-token:";

    public void save(Long userId, String refreshToken, long expiration){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeAndBlankCheck(refreshToken, "refreshToken");
        CheckConfig.npeAndNegativeCheck(expiration, "expiration");

        stringRedisTemplate.opsForValue().set(
                refreshTokenKey + userId,
                refreshToken,
                Duration.ofMillis(expiration)
        );
    }

    public String find(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        return stringRedisTemplate.opsForValue().get(
                refreshTokenKey + userId
        );
    }

    public boolean matches(Long userId, String refreshToken){
        CheckConfig.npeCheck(userId, "userId");
        CheckConfig.npeAndBlankCheck(refreshToken, "refreshToken");

        String storedRefreshToken = find(userId);
        if(storedRefreshToken == null){
            throw new NotFoundException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        return storedRefreshToken.equals(refreshToken);
    }

    public void delete(Long userId){
        CheckConfig.npeCheck(userId, "userId");

        stringRedisTemplate.delete(refreshTokenKey + userId);
    }
}
