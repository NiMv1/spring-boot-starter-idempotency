package io.github.nimv1.idempotency.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Аннотация для обеспечения идемпотентности запросов.
 * 
 * <p>Применяется к методам контроллера или сервиса для предотвращения
 * повторной обработки одинаковых запросов в течение заданного времени.</p>
 * 
 * <p><b>Пример использования:</b></p>
 * <pre>{@code
 * @PostMapping("/payment")
 * @Idempotent(key = "#request.transactionId", ttl = 24, timeUnit = TimeUnit.HOURS)
 * public PaymentResponse processPayment(@RequestBody PaymentRequest request) {
 *     // Этот метод будет выполнен только один раз для каждого transactionId
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
     * SpEL выражение для генерации ключа идемпотентности.
     * 
     * <p>Поддерживаемые переменные:</p>
     * <ul>
     *   <li>{@code #paramName} - параметр метода по имени</li>
     *   <li>{@code #p0, #p1, ...} - параметры по индексу</li>
     *   <li>{@code #root.methodName} - имя метода</li>
     * </ul>
     * 
     * @return SpEL выражение для ключа
     */
    String key();

    /**
     * Время жизни ключа идемпотентности.
     * 
     * @return время жизни (по умолчанию 1)
     */
    long ttl() default 1;

    /**
     * Единица измерения времени жизни.
     * 
     * @return единица времени (по умолчанию HOURS)
     */
    TimeUnit timeUnit() default TimeUnit.HOURS;

    /**
     * Префикс для ключа в Redis.
     * 
     * @return префикс ключа (по умолчанию "idempotent:")
     */
    String prefix() default "idempotent:";

    /**
     * Сообщение об ошибке при дублирующем запросе.
     * 
     * @return сообщение об ошибке
     */
    String message() default "Duplicate request detected";
}
