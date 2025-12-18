package io.github.nimv1.idempotency.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Сервис для работы с идемпотентными ключами в Redis.
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
     * Проверяет, существует ли ключ идемпотентности.
     * 
     * @param key ключ для проверки
     * @return true если ключ существует (дубликат), false если нет
     */
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Сохраняет ключ идемпотентности с заданным TTL.
     * 
     * @param key ключ для сохранения
     * @param ttl время жизни
     * @param timeUnit единица измерения времени
     * @return true если ключ успешно сохранён (не существовал), false если уже существует
     */
    public boolean setIfAbsent(String key, long ttl, TimeUnit timeUnit) {
        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, "1", ttl, timeUnit)
        );
    }

    /**
     * Удаляет ключ идемпотентности.
     * 
     * @param key ключ для удаления
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
