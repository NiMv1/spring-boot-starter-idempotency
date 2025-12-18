# Spring Boot Starter Idempotency

[![Java](https://img.shields.io/badge/Java-17+-00FFFF?style=for-the-badge&logo=openjdk&logoColor=black)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2+-FF00FF?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Redis](https://img.shields.io/badge/Redis-Required-00FF00?style=for-the-badge&logo=redis&logoColor=black)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

> 🔒 **Spring Boot starter for idempotent request handling with Redis support**

**English** | [Русский](docs/README_RU.md)

## ✨ Features

- **Simple annotation** `@Idempotent` for methods
- **SpEL expressions** for key generation
- **Configurable TTL** per method
- **Automatic key removal** on error (retry-friendly)
- **Full integration** with Spring Boot 3.x

## 🚀 Quick Start

### 1. Add dependency

```xml
<dependency>
    <groupId>io.github.nimv1</groupId>
    <artifactId>spring-boot-starter-idempotency</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Configure Redis

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### 3. Use the annotation

```java
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @PostMapping
    @Idempotent(key = "#request.transactionId", ttl = 24, timeUnit = TimeUnit.HOURS)
    public PaymentResponse processPayment(@RequestBody PaymentRequest request) {
        // This method will be executed only once for each transactionId
        return paymentService.process(request);
    }
}
```

## 📖 Documentation

### @Idempotent Annotation

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `key` | String | **required** | SpEL expression for key generation |
| `ttl` | long | 1 | Key time-to-live |
| `timeUnit` | TimeUnit | HOURS | Time unit for TTL |
| `prefix` | String | "idempotent:" | Key prefix in Redis |
| `message` | String | "Duplicate request detected" | Error message |

### SpEL Expressions

```java
// Parameter by name
@Idempotent(key = "#transactionId")

// Nested property
@Idempotent(key = "#request.orderId")

// Combined parameters
@Idempotent(key = "#userId + ':' + #orderId")

// Parameter by index
@Idempotent(key = "#p0.id")
```

### Configuration

```yaml
idempotency:
  enabled: true              # Enable/disable (default true)
  default-ttl: 1             # Default TTL
  default-time-unit: HOURS   # Default time unit
  key-prefix: "idempotent:"  # Redis key prefix
```

### Error Handling

On duplicate request, `IdempotencyException` is thrown:

```java
@ExceptionHandler(IdempotencyException.class)
public ResponseEntity<ErrorResponse> handleIdempotency(IdempotencyException ex) {
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(ex.getMessage(), ex.getIdempotencyKey()));
}
```

## 🔧 How It Works

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

## 📝 Usage Examples

### Payment Service

```java
@Service
public class PaymentService {

    @Idempotent(key = "#payment.transactionId", ttl = 24, timeUnit = TimeUnit.HOURS)
    public PaymentResult processPayment(Payment payment) {
        // Safe from duplicate processing
        return gateway.charge(payment);
    }
}
```

### Notification Service

```java
@Service
public class NotificationService {

    @Idempotent(key = "#userId + ':' + #eventType", ttl = 5, timeUnit = TimeUnit.MINUTES)
    public void sendNotification(String userId, String eventType, String message) {
        // User will receive only one notification per 5 minutes
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
