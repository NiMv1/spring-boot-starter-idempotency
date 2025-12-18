package io.github.nimv1.idempotency;

import io.github.nimv1.idempotency.service.IdempotencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for IdempotencyService.
 * 
 * @author NiMv1
 */
class IdempotencyServiceTest {

    private IdempotencyService idempotencyService;
    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        
        idempotencyService = new IdempotencyService(redisTemplate);
    }

    @Test
    void shouldReturnTrueWhenKeyExists() {
        // Given
        String key = "idempotent:test:123";
        when(redisTemplate.hasKey(key)).thenReturn(true);
        
        // When
        boolean exists = idempotencyService.exists(key);
        
        // Then
        assertTrue(exists);
        verify(redisTemplate).hasKey(key);
    }

    @Test
    void shouldReturnFalseWhenKeyDoesNotExist() {
        // Given
        String key = "idempotent:test:456";
        when(redisTemplate.hasKey(key)).thenReturn(false);
        
        // When
        boolean exists = idempotencyService.exists(key);
        
        // Then
        assertFalse(exists);
    }

    @Test
    void shouldSetKeyIfAbsent() {
        // Given
        String key = "idempotent:test:789";
        when(valueOperations.setIfAbsent(key, "1", 1, TimeUnit.HOURS)).thenReturn(true);
        
        // When
        boolean result = idempotencyService.setIfAbsent(key, 1, TimeUnit.HOURS);
        
        // Then
        assertTrue(result);
        verify(valueOperations).setIfAbsent(key, "1", 1, TimeUnit.HOURS);
    }

    @Test
    void shouldReturnFalseWhenKeyAlreadyExists() {
        // Given
        String key = "idempotent:test:existing";
        when(valueOperations.setIfAbsent(key, "1", 1, TimeUnit.HOURS)).thenReturn(false);
        
        // When
        boolean result = idempotencyService.setIfAbsent(key, 1, TimeUnit.HOURS);
        
        // Then
        assertFalse(result);
    }

    @Test
    void shouldDeleteKey() {
        // Given
        String key = "idempotent:test:delete";
        
        // When
        idempotencyService.delete(key);
        
        // Then
        verify(redisTemplate).delete(key);
    }
}
