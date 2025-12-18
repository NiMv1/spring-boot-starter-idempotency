package io.github.nimv1.idempotency.aspect;

import io.github.nimv1.idempotency.annotation.Idempotent;
import io.github.nimv1.idempotency.config.IdempotencyProperties;
import io.github.nimv1.idempotency.exception.IdempotencyException;
import io.github.nimv1.idempotency.service.IdempotencyKeyGenerator;
import io.github.nimv1.idempotency.service.IdempotencyService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Аспект для обработки аннотации {@link Idempotent}.
 * 
 * <p>Перехватывает вызовы методов с аннотацией @Idempotent и проверяет,
 * не был ли запрос уже обработан.</p>
 * 
 * @author NiMv1
 * @since 1.0.0
 */
@Aspect
@Component
public class IdempotencyAspect {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyAspect.class);

    private final IdempotencyService idempotencyService;
    private final IdempotencyKeyGenerator keyGenerator;
    private final IdempotencyProperties properties;

    public IdempotencyAspect(
            IdempotencyService idempotencyService,
            IdempotencyKeyGenerator keyGenerator,
            IdempotencyProperties properties) {
        this.idempotencyService = idempotencyService;
        this.keyGenerator = keyGenerator;
        this.properties = properties;
    }

    /**
     * Обрабатывает методы с аннотацией @Idempotent.
     * 
     * @param joinPoint точка соединения
     * @param idempotent аннотация идемпотентности
     * @return результат выполнения метода
     * @throws Throwable если метод выбросил исключение
     */
    @Around("@annotation(idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        if (!properties.isEnabled()) {
            return joinPoint.proceed();
        }

        String prefix = idempotent.prefix().isEmpty() ? properties.getKeyPrefix() : idempotent.prefix();
        String key = keyGenerator.generateKey(joinPoint, idempotent.key(), prefix);
        
        long ttl = idempotent.ttl() > 0 ? idempotent.ttl() : properties.getDefaultTtl();
        TimeUnit timeUnit = idempotent.timeUnit();

        log.debug("Checking idempotency for key: {}", key);

        boolean isNew = idempotencyService.setIfAbsent(key, ttl, timeUnit);
        
        if (!isNew) {
            log.warn("Duplicate request detected for key: {}", key);
            throw new IdempotencyException(idempotent.message(), key);
        }

        try {
            Object result = joinPoint.proceed();
            log.debug("Request processed successfully for key: {}", key);
            return result;
        } catch (Exception e) {
            // При ошибке удаляем ключ, чтобы можно было повторить запрос
            idempotencyService.delete(key);
            log.debug("Request failed, idempotency key removed: {}", key);
            throw e;
        }
    }
}
