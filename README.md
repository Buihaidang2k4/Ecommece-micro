# MyShop Ecommerce Microservices

## Architecture (coarse)
| Module | Port | Role |
|---|---|---|
| `service-registry` | 8762 | Eureka |
| `config-service` | 8889 | Spring Cloud Config (owns all configs) |
| `api-gateway` | 8090 | Routing, CORS, JWT (HS256), Redis rate-limiting |
| `auth-service` | 8091 | JWT (+ `userId` claim), RBAC, Google, OTP, Kafka `UserRegistered` |
| `core-service` | 8092 | Profile, catalog, inventory, cart, order, coupon, report, review |
| `file-service` | 8094 | MinIO upload / presign / delete (JWT-secured for writes) |
| `notification-service` | 8095 | MongoDB + Kafka consumer + email (JWT-secured) |
| `payment-service` | 8096 | VNPay integration, outbox-based event publishing |
| `commons/` | — | shared library (constants, dto, events, exception, security) |

## API prefix
- YAML (gateway routes): `api.prefix` in `config-service/.../config-*.commons/application.yml`
- Java (`@RequestMapping` / security): `ApiPrefixes.V1` in `commons`

## Swagger UI
Open per service (not via gateway):
| Service | URL |
|---|---|
| auth | http://localhost:8091/swagger-ui/index.html |
| core | http://localhost:8092/swagger-ui/index.html |
| file | http://localhost:8094/swagger-ui/index.html |
| notification | http://localhost:8095/swagger-ui/index.html |
| payment | http://localhost:8096/swagger-ui/index.html |

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
| MinIO | 9000 / 9001 |
| MongoDB | 27017 |

## Run order
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

.\mvnw.cmd -pl service-registry spring-boot:run
.\mvnw.cmd -pl config-service spring-boot:run
.\mvnw.cmd -pl auth-service spring-boot:run
.\mvnw.cmd -pl core-service spring-boot:run
.\mvnw.cmd -pl file-service spring-boot:run
.\mvnw.cmd -pl notification-service spring-boot:run
.\mvnw.cmd -pl payment-service spring-boot:run
.\mvnw.cmd -pl api-gateway spring-boot:run
```

Default admin: `admin@gmail.com` / `Admin@123`

## Transactional Outbox

All services use the **outbox pattern** to eliminate dual-write Kafka from the business path:
- Business logic writes an `outbox` row in the same DB transaction
- `OutboxRelay` (`@Scheduled`) polls unpublished rows and sends to Kafka, then marks published

### Outbox Relay config (in each service's dev yml)
```yaml
myshop:
  outbox:
    relay-enabled: true
    relay-delay-ms: 1000
```

## Key Endpoints
```text
POST /api/v1/auth/login | register  → Kafka → notification + core profile/cart
GET  /api/v1/auth/me/permissions    → list current user permissions
GET  /api/v1/admin/permissions      → list all permissions (requires permission:manage)
GET  /api/v1/products/**
POST /api/v1/orders/buy-now | /place-order
POST /api/v1/payment/**
POST /api/v1/files/upload           → requires media:upload permission
GET  /api/v1/files/presign          → public (download)
GET  /api/v1/notifications          → requires authentication
GET  /api/v1/report/monthly-revenue → requires report:revenue permission
GET  /api/v1/report/product-revenue → requires report:revenue permission
```

## Gateway Rate Limiting
The API Gateway uses Redis-backed `RequestRateLimiter`:
- **replenishRate**: 50 requests/sec
- **burstCapacity**: 100 requests
- Key resolver: client IP address
