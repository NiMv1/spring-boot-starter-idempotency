package io.github.nimv1.idempotency;

import io.github.nimv1.idempotency.service.IdempotencyKeyGenerator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for IdempotencyKeyGenerator.
 * 
 * @author NiMv1
 */
class IdempotencyKeyGeneratorTest {

    private IdempotencyKeyGenerator keyGenerator;

    @BeforeEach
    void setUp() {
        keyGenerator = new IdempotencyKeyGenerator();
    }

    @Test
    void shouldGenerateKeyFromSimpleParameter() throws NoSuchMethodException {
        // Given
        JoinPoint joinPoint = createMockJoinPoint("processPayment", new Object[]{"tx-123"});
        
        // When
        String key = keyGenerator.generateKey(joinPoint, "#p0", "idempotent:");
        
        // Then
        assertTrue(key.startsWith("idempotent:"));
        assertTrue(key.contains("tx-123"));
    }

    @Test
    void shouldGenerateKeyWithPrefix() throws NoSuchMethodException {
        // Given
        JoinPoint joinPoint = createMockJoinPoint("processPayment", new Object[]{"tx-456"});
        
        // When
        String key = keyGenerator.generateKey(joinPoint, "#p0", "custom:");
        
        // Then
        assertTrue(key.startsWith("custom:"));
    }

    @Test
    void shouldThrowExceptionWhenKeyIsNull() throws NoSuchMethodException {
        // Given
        JoinPoint joinPoint = createMockJoinPoint("processPayment", new Object[]{null});
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
                keyGenerator.generateKey(joinPoint, "#p0", "idempotent:")
        );
    }

    private JoinPoint createMockJoinPoint(String methodName, Object[] args) throws NoSuchMethodException {
        JoinPoint joinPoint = mock(JoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        
        Method method = TestClass.class.getMethod(methodName, String.class);
        
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getArgs()).thenReturn(args);
        when(joinPoint.getTarget()).thenReturn(new TestClass());
        
        return joinPoint;
    }

    static class TestClass {
        public String processPayment(String transactionId) {
            return "processed: " + transactionId;
        }
    }
}
