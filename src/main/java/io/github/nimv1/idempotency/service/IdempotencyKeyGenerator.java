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
 * Idempotency key generator based on SpEL expressions.
 * 
 * @author NiMv1
 * @since 1.0.0
 */
@Component
public class IdempotencyKeyGenerator {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * Generates an idempotency key based on a SpEL expression.
     * 
     * @param joinPoint AOP join point
     * @param keyExpression SpEL expression for the key
     * @param prefix key prefix
     * @return generated key
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
