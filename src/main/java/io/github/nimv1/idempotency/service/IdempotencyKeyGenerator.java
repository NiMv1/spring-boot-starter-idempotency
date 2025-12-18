package io.github.nimv1.idempotency.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Генератор ключей идемпотентности на основе SpEL выражений.
 * 
 * @author NiMv1
 * @since 1.0.0
 */
@Component
public class IdempotencyKeyGenerator {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * Генерирует ключ идемпотентности на основе SpEL выражения.
     * 
     * @param joinPoint точка соединения AOP
     * @param keyExpression SpEL выражение для ключа
     * @param prefix префикс ключа
     * @return сгенерированный ключ
     */
    public String generateKey(JoinPoint joinPoint, String keyExpression, String prefix) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Object target = joinPoint.getTarget();

        EvaluationContext context = new MethodBasedEvaluationContext(
                target, method, args, parameterNameDiscoverer
        );

        Object keyValue = parser.parseExpression(keyExpression).getValue(context);
        
        if (keyValue == null) {
            throw new IllegalArgumentException(
                    "Idempotency key expression '" + keyExpression + "' evaluated to null"
            );
        }

        return prefix + method.getDeclaringClass().getSimpleName() 
                + ":" + method.getName() 
                + ":" + keyValue.toString();
    }
}
