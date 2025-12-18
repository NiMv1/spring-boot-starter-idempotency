# Spring Boot Starter Idempotency

[![Java](https://img.shields.io/badge/Java-17+-00FFFF?style=for-the-badge&logo=openjdk&logoColor=black)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2+-FF00FF?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Redis](https://img.shields.io/badge/Redis-Required-00FF00?style=for-the-badge&logo=redis&logoColor=black)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

> 🔒 **Spring Boot стартер для обеспечения идемпотентности запросов с использованием Redis**

## ✨ Возможности

- **Простая аннотация** `@Idempotent` для методов
- **SpEL выражения** для генерации ключей
- **Настраиваемый TTL** для каждого метода
- **Автоматическое удаление ключа** при ошибке (retry-friendly)
- **Полная интеграция** с Spring Boot 3.x

## 🚀 Быстрый старт

### 1. Добавьте зависимость

```xml
<dependency>
    <groupId>io.github.nimv1</groupId>
    <artifactId>spring-boot-starter-idempotency</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Настройте Redis

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### 3. Используйте аннотацию

```java
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @PostMapping
    @Idempotent(key = "#request.transactionId", ttl = 24, timeUnit = TimeUnit.HOURS)
    public PaymentResponse processPayment(@RequestBody PaymentRequest request) {
        // Этот метод будет выполнен только один раз для каждого transactionId
        return paymentService.process(request);
    }
}
```

## 📖 Документация

### Аннотация @Idempotent

| Параметр | Тип | По умолчанию | Описание |
|----------|-----|--------------|----------|
| `key` | String | **required** | SpEL выражение для генерации ключа |
| `ttl` | long | 1 | Время жизни ключа |
| `timeUnit` | TimeUnit | HOURS | Единица измерения времени |
| `prefix` | String | "idempotent:" | Префикс ключа в Redis |
| `message` | String | "Duplicate request detected" | Сообщение об ошибке |

### SpEL выражения

```java
// Параметр по имени
@Idempotent(key = "#transactionId")

// Вложенное свойство
@Idempotent(key = "#request.orderId")

// Комбинация параметров
@Idempotent(key = "#userId + ':' + #orderId")

// Параметр по индексу
@Idempotent(key = "#p0.id")
```

### Конфигурация

```yaml
idempotency:
  enabled: true              # Включить/выключить (по умолчанию true)
  default-ttl: 1             # TTL по умолчанию
  default-time-unit: HOURS   # Единица времени по умолчанию
  key-prefix: "idempotent:"  # Префикс ключей в Redis
```

### Обработка ошибок

При дублирующем запросе выбрасывается `IdempotencyException`:

```java
@ExceptionHandler(IdempotencyException.class)
public ResponseEntity<ErrorResponse> handleIdempotency(IdempotencyException ex) {
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(ex.getMessage(), ex.getIdempotencyKey()));
}
```

## 🔧 Как это работает

```
┌─────────────┐     ┌──────────────────┐     ┌─────────────┐
│   Request   │────▶│ IdempotencyAspect│────▶│    Redis    │
└─────────────┘     └──────────────────┘     └─────────────┘
                            │                       │
                            ▼                       │
                    ┌───────────────┐               │
                    │ Key exists?   │◀──────────────┘
                    └───────────────┘
                      │         │
                     Yes        No
                      │         │
                      ▼         ▼
              ┌───────────┐  ┌───────────────┐
              │  Throw    │  │ Execute method│
              │ Exception │  │ & save key    │
              └───────────┘  └───────────────┘
```

## 📝 Примеры использования

### Платёжный сервис

```java
@Service
public class PaymentService {

    @Idempotent(key = "#payment.transactionId", ttl = 24, timeUnit = TimeUnit.HOURS)
    public PaymentResult processPayment(Payment payment) {
        // Безопасно от дублирования
        return gateway.charge(payment);
    }
}
```

### Отправка уведомлений

```java
@Service
public class NotificationService {

    @Idempotent(key = "#userId + ':' + #eventType", ttl = 5, timeUnit = TimeUnit.MINUTES)
    public void sendNotification(String userId, String eventType, String message) {
        // Один пользователь получит только одно уведомление за 5 минут
        emailService.send(userId, message);
    }
}
```

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**NiMv1** - [GitHub](https://github.com/NiMv1) | [Portfolio](https://nimv1.github.io)
