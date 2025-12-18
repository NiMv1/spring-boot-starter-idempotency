package io.github.nimv1.idempotency.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * Конфигурационные свойства для идемпотентности.
 * 
 * <p>Настраиваются через application.yml:</p>
 * <pre>{@code
 * idempotency:
 *   enabled: true
 *   default-ttl: 1
 *   default-time-unit: HOURS
 *   key-prefix: "idempotent:"
 * }</pre>
 * 
 * @author NiMv1
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "idempotency")
public class IdempotencyProperties {

    /**
     * Включить/выключить проверку идемпотентности.
     */
    private boolean enabled = true;

    /**
     * Время жизни ключа по умолчанию.
     */
    private long defaultTtl = 1;

    /**
     * Единица измерения времени по умолчанию.
     */
    private TimeUnit defaultTimeUnit = TimeUnit.HOURS;

    /**
     * Префикс для ключей в Redis.
     */
    private String keyPrefix = "idempotent:";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getDefaultTtl() {
        return defaultTtl;
    }

    public void setDefaultTtl(long defaultTtl) {
        this.defaultTtl = defaultTtl;
    }

    public TimeUnit getDefaultTimeUnit() {
        return defaultTimeUnit;
    }

    public void setDefaultTimeUnit(TimeUnit defaultTimeUnit) {
        this.defaultTimeUnit = defaultTimeUnit;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }
}
