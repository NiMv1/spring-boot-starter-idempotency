package io.github.nimv1.idempotency.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Annotation to ensure request idempotency.
 * 
 * <p>Applied to controller or service methods to prevent
 * duplicate processing of identical requests within a specified time period.</p>
 * 
 * <p><b>Usage example:</b></p>
 * <pre>{@code
 * @PostMapping("/payment")
 * @Idempotent(key = "#request.transactionId", ttl = 24, timeUnit = TimeUnit.HOURS)
 * public PaymentResponse processPayment(@RequestBody PaymentRequest request) {
 *     // This method will be executed only once for each transactionId
 *     return paymentService.process(request);
 * }
 * }</pre>
 * 
 * @author NiMv1
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    /**
     * SpEL expression for generating the idempotency key.
     * 
     * <p>Supported variables:</p>
     * <ul>
     *   <li>{@code #paramName} - method parameter by name</li>
     *   <li>{@code #p0, #p1, ...} - parameters by index</li>
     *   <li>{@code #root.methodName} - method name</li>
     * </ul>
     * 
     * @return SpEL expression for the key
     */
    String key();

    /**
     * Time-to-live for the idempotency key.
     * 
     * @return TTL value (default 1)
     */
    long ttl() default 1;

    /**
     * Time unit for TTL.
     * 
     * @return time unit (default HOURS)
     */
    TimeUnit timeUnit() default TimeUnit.HOURS;

    /**
     * Prefix for the key in Redis.
     * 
     * @return key prefix (default "idempotent:")
     */
    String prefix() default "idempotent:";

    /**
     * Error message for duplicate requests.
     * 
     * @return error message
     */
    String message() default "Duplicate request detected";
}
