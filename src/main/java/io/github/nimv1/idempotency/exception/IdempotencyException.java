package io.github.nimv1.idempotency.exception;

/**
 * Исключение, выбрасываемое при обнаружении дублирующего запроса.
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
     * Возвращает ключ идемпотентности, вызвавший исключение.
     * 
     * @return ключ идемпотентности
     */
    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
