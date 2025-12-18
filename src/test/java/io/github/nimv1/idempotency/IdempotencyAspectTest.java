package io.github.nimv1.idempotency;

import io.github.nimv1.idempotency.annotation.Idempotent;
import io.github.nimv1.idempotency.config.IdempotencyAutoConfiguration;
import io.github.nimv1.idempotency.exception.IdempotencyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для IdempotencyAspect.
 * 
 * @author NiMv1
 */
@SpringBootTest(classes = {
        RedisAutoConfiguration.class,
        IdempotencyAutoConfiguration.class,
        IdempotencyAspectTest.TestService.class
})
class IdempotencyAspectTest {

    @Autowired
    private TestService testService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        // Очищаем Redis перед каждым тестом
        testService.resetCounter();
        redisTemplate.keys("idempotent:*").forEach(redisTemplate::delete);
    }

    @Test
    void shouldExecuteMethodOnce() {
        String transactionId = "tx-123";
        
        String result = testService.processPayment(transactionId);
        
        assertEquals("processed: tx-123", result);
        assertEquals(1, testService.getCallCount());
    }

    @Test
    void shouldThrowExceptionOnDuplicateRequest() {
        String transactionId = "tx-456";
        
        // Первый вызов - успешно
        testService.processPayment(transactionId);
        assertEquals(1, testService.getCallCount());
        
        // Второй вызов - должен выбросить исключение
        assertThrows(IdempotencyException.class, () -> 
                testService.processPayment(transactionId)
        );
        
        // Метод не должен быть вызван повторно
        assertEquals(1, testService.getCallCount());
    }

    @Test
    void shouldAllowDifferentKeys() {
        testService.processPayment("tx-1");
        testService.processPayment("tx-2");
        testService.processPayment("tx-3");
        
        assertEquals(3, testService.getCallCount());
    }

    @Test
    void shouldRemoveKeyOnException() {
        String transactionId = "tx-error";
        
        // Первый вызов - выбросит исключение
        assertThrows(RuntimeException.class, () -> 
                testService.processWithError(transactionId)
        );
        
        // Второй вызов - должен работать (ключ удалён)
        assertDoesNotThrow(() -> 
                testService.processWithError(transactionId)
        );
    }

    @Service
    static class TestService {
        
        private final AtomicInteger callCount = new AtomicInteger(0);
        private boolean shouldThrow = true;

        @Idempotent(key = "#transactionId", ttl = 1, timeUnit = TimeUnit.MINUTES)
        public String processPayment(String transactionId) {
            callCount.incrementAndGet();
            return "processed: " + transactionId;
        }

        @Idempotent(key = "#transactionId", ttl = 1, timeUnit = TimeUnit.MINUTES)
        public String processWithError(String transactionId) {
            if (shouldThrow) {
                shouldThrow = false;
                throw new RuntimeException("Simulated error");
            }
            return "success";
        }

        public int getCallCount() {
            return callCount.get();
        }

        public void resetCounter() {
            callCount.set(0);
            shouldThrow = true;
        }
    }
}
