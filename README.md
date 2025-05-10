# 📲 MobbScan Integration - Spring Boot

Este proyecto permite integrar una aplicación Spring Boot con la API de MobbScan para procesos de verificación de identidad digital. A través de endpoints seguros con JWT, se puede autenticar, lanzar procesos de onboarding y consultar resultados mediante la pasarela de Mobbeel.

---

## ⚙️ Requisitos

- Java 17 o superior
- Maven o Gradle
- MongoDB (local o en la nube)
- Spring Boot 3.x
- Cuenta activa en MobbScan con credenciales (API Key y Secret)

---

## 📦 Estructura general
```curl
src/
├── main/
│ ├── java/
│ │ └── com.mobbScan_integration/
│ │ ├── MobbScanApplication.java
│ │ ├── controller/
│ │ │ └── AuthController.java
│ │ │ └── OnboardingController.java
│ │ ├── dto/
│ │ │ └── LoginRequest.java, OnboardingRequest.java, JwtAuthenticationResponse.java
│ │ ├── security/
│ │ │ └── SecurityConfiguration.java, JwtTokenProvider.java, JwtAuthenticationFilter.java
│ │ ├── service/
│ │ │ └── UserService.java
│ │ ├── repository/
│ │ │ └── UserRepository.java
│ └── resources/
│ ├── application.properties

```

---

## 🔐 Autenticación

1. **Registro de usuario:**
```curl
POST /api/auth/register \
Body: { "username": "user", "password": "pass" }
```

2. **Inicio de sesión (login):**
```curl
POST /api/auth/login
Body: { "username": "user", "password": "pass" }#
```
🔁 Devuelve un token JWT válido para usar en las siguientes peticiones con header:

Authorization: Bearer <token>


---

## 🔁 Flujo de integración con MobbScan

### 1. Obtener `access_token` de MobbScan
```curl
POST /onboarding/authenticate
Body:
{
"api_key": "TU_API_KEY",
"api_secret": "TU_API_SECRET"
}

```

### 2. Iniciar proceso de onboarding
```curl
POST /onboarding/create
Headers: Authorization: Bearer <access_token>
Body:
{
"countryId": "ESP",
"docType": "IDCard",
"redirectUrl": "https://tusitio.com/callback",
"scanId": "uuid-generado",
"verificationExtraData": {
"campo": "valor"
}
}
```


### 3. Obtener estado/resultados:
- `/onboarding/getVerificationProcessData/{verificationId}`
- `/onboarding/checkVerificationProcessResult/{verificationId}`

---

## 🛠️ Configuración

### 📍 `application.properties`

```properties
server.port=8081

jwt.secret=claveSuperSecretaDe32CaracteresOmas
jwt.expiration.ms=86400000
jwt.issuer=mobbScan-integration

mobbscan.api.key=TU_API_KEY
mobbscan.api.secret=TU_API_SECRET
mobbscan.api.gateway=https://gateway-dev.mobbeel.com
```

