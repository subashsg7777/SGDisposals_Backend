package com.subash.SGDisposals.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService implements IRateLimitService{

    private final StringRedisTemplate stringRedisTemplate;


    @Override
    public boolean isAllowed(String key, int limit, int windowSeconds) {
        Long count = stringRedisTemplate.opsForValue().increment(key);

        if(count == 1) {
            stringRedisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }
        return count <= limit;
    }
}
