package io.github.nimv1.idempotency.config;

import io.github.nimv1.idempotency.aspect.IdempotencyAspect;
import io.github.nimv1.idempotency.service.IdempotencyKeyGenerator;
import io.github.nimv1.idempotency.service.IdempotencyService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Автоконфигурация для Spring Boot Starter Idempotency.
 * 
 * <p>Автоматически настраивает все необходимые бины при наличии Redis.</p>
 * 
 * @author NiMv1
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnClass(StringRedisTemplate.class)
@ConditionalOnProperty(prefix = "idempotency", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(IdempotencyProperties.class)
public class IdempotencyAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IdempotencyKeyGenerator idempotencyKeyGenerator() {
        return new IdempotencyKeyGenerator();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotencyService idempotencyService(StringRedisTemplate redisTemplate) {
        return new IdempotencyService(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotencyAspect idempotencyAspect(
            IdempotencyService idempotencyService,
            IdempotencyKeyGenerator keyGenerator,
            IdempotencyProperties properties) {
        return new IdempotencyAspect(idempotencyService, keyGenerator, properties);
    }
}
