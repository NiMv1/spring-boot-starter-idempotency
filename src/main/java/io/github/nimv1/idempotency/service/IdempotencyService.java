package io.github.nimv1.idempotency.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service for working with idempotency keys in Redis.
 * 
 * @author NiMv1
 * @since 1.0.0
 */
@Service
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;

    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Checks if an idempotency key exists.
     * 
     * @param key key to check
     * @return true if key exists (duplicate), false otherwise
     */
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Saves an idempotency key with the specified TTL.
     * 
     * @param key key to save
     * @param ttl time-to-live
     * @param timeUnit time unit
     * @return true if key was successfully saved (did not exist), false if already exists
     */
    public boolean setIfAbsent(String key, long ttl, TimeUnit timeUnit) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, "1", ttl, timeUnit)
        );
    }

    /**
     * Deletes an idempotency key.
     * 
     * @param key key to delete
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
