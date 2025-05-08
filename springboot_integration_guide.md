# Spring Boot Integration Project Guide

## ✨ Overview
This guide outlines the architecture and components needed to build a Spring Boot integration project with:

- JWT Authentication
- Integration with an external service via Feign
- MySQL Database (Docker-based)
- Logging, error handling, and validation

---

## 🔐 JWT Authentication

### Dependencies (Maven)
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### Guide
- [JWT Authentication with Spring Security - Baeldung](https://www.baeldung.com/spring-security-oauth-jwt)

---

## 🔗 Integration with External Service (Feign Client)

### Dependencies
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### Sample Client
```java
@FeignClient(name = "external-service", url = "http://host:port")
public interface ExternalServiceClient {
    @GetMapping("/api/some-endpoint")
    ResponseEntity<SomeDto> getSomething();
}
```

### Guides
- [Spring Cloud OpenFeign Docs](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)
- [Feign Client + JWT Integration](https://www.baeldung.com/spring-cloud-openfeign)

---

## 🛢️ Database Integration (JPA + MySQL in Docker)

### Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

### application.yml
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: secret
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL8Dialect
```

### Docker Compose (MySQL)
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: secret
      MYSQL_DATABASE: mydb
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

---

## 🛠️ Utilities

### Validation
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### Logging & Global Error Handling
- Use SLF4J + Logback for logging
- Create `@ControllerAdvice` classes for centralized error handling

---

## 🧪 Testing

### Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Guide
- [Testing in Spring Boot](https://www.baeldung.com/spring-boot-testing)

---

## 📁 Project Structure
```
com.example.integrationproject
├── config/           → Security, Feign, JWT config
├── controller/       → REST endpoints
├── service/          → Business logic
├── repository/       → JPA Repositories
├── model/            → DTOs and Entities
├── security/         → JWT Filters, Providers
├── client/           → Feign Clients
└── IntegrationProjectApplication.java
```
