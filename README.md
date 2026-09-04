# MyShop Ecommerce Microservices

## Architecture (coarse)
| Module | Port | Role |
|---|---|---|
| `service-registry` | 8761 | Eureka |
| `config-service` | 8888 | Spring Cloud Config (owns all configs) |
| `api-gateway` | 8080 | Routing, CORS, JWT (HS256) |
| `auth-service` | 8081 | JWT, RBAC, Google, OTP, Kafka `UserRegistered` |
| `file-service` | 8084 | MinIO upload / presign / delete |
| `notification-service` | 8085 | MongoDB + Kafka consumer + email |
| `core-service` | 8082 | Phase 2 |
| `payment-service` | 8086 | Phase 3 |
| `commons/` | — | shared DTO, security, events, exception |

## Config layout (`config-service`)
```text
config-service/src/main/resources/
├── application.yml
├── config-dev.commons/ / config-prod.commons/ / config-test.commons/
└── config/{api-gateway,auth-service,core-service,payment-service,file-service,notification-service,service-registry}/
```

Native search: `classpath:/config/{application},classpath:/config-{profile}.commons`  
Example: `http://localhost:8888/auth-service/dev`

## Build
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
.\mvnw.cmd clean install -DskipTests
```

## Infrastructure
```bash
docker compose up -d
```

| Service | Host port |
|---|---|
| MySQL | **3307** (`auth_db`, `core_db`, `payment_db`) |
| Redis | **6380** |
| Kafka | 9092 |
| Kafka Connect | 8083 |
| MinIO | 9000 / 9001 |
| MongoDB | 27017 |

## Run order (Phase 0 + 1)
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# Platform
.\mvnw.cmd -pl service-registry spring-boot:run
.\mvnw.cmd -pl config-service spring-boot:run

# Phase 1
.\mvnw.cmd -pl auth-service spring-boot:run
.\mvnw.cmd -pl file-service spring-boot:run
.\mvnw.cmd -pl notification-service spring-boot:run

# Gateway last
.\mvnw.cmd -pl api-gateway spring-boot:run
```

Default admin (seeded): `admin@gmail.com` / `Admin@123`

### Smoke checks
```text
POST http://localhost:8080/api/v1/auth/login
POST http://localhost:8080/api/v1/auth/register   → Kafka → notification welcome log in Mongo
POST http://localhost:8080/api/v1/files/upload
GET  http://localhost:8080/api/v1/notifications
```

Optional env: `JWT_SECRET`, `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET`, `EMAIL_USERNAME` / `EMAIL_PASSWORD`, MinIO vars.

## Phases
- **0** Platform — done  
- **1** auth / file / notification — done (build verified)  
- **2** core-service (catalog, cart, order, inventory)  
- **3** payment-service (VNPay)  
- **4** stabilize (outbox/CDC, hardening)
