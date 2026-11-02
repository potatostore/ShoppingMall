package com.shopping_mall_api.repository.redis;

import com.shopping_mall_api.global.config.CheckConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

// 로그아웃한 access token의 jti를 남은 만료 시간만큼만 보관 (만료되면 어차피 무효라 Redis에서 자동 삭제)
@Repository
@RequiredArgsConstructor
public class AccessTokenBlacklistRepository {
    private static final String KEY_PREFIX = "blacklist:access:";

    private final StringRedisTemplate stringRedisTemplate;

    public void add(String jti, long remainingMillis){
        CheckConfig.npeAndBlankCheck(jti, "jti");

        if(remainingMillis <= 0){
            return;
        }

        stringRedisTemplate.opsForValue().set(KEY_PREFIX + jti, "1", Duration.ofMillis(remainingMillis));
    }

    public boolean contains(String jti){
        CheckConfig.npeAndBlankCheck(jti, "jti");

        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(KEY_PREFIX + jti));
    }
}
