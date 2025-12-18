package io.github.nimv1.idempotency.exception;

/**
 * Exception thrown when a duplicate request is detected.
 * 
 * @author NiMv1
 * @since 1.0.0
 */
public class IdempotencyException extends RuntimeException {

    private final String idempotencyKey;

    public IdempotencyException(String message, String idempotencyKey) {
        super(message);
        this.idempotencyKey = idempotencyKey;
    }

    /**
     * Returns the idempotency key that caused the exception.
     * 
     * @return idempotency key
     */
    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
