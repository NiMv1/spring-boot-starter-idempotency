package io.github.nimv1.idempotency.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * Configuration properties for idempotency.
 * 
 * <p>Configured via application.yml:</p>
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
     * Enable/disable idempotency checking.
     */
    private boolean enabled = true;

    /**
     * Default key TTL.
     */
    private long defaultTtl = 1;

    /**
     * Default time unit.
     */
    private TimeUnit defaultTimeUnit = TimeUnit.HOURS;

    /**
     * Prefix for keys in Redis.
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
