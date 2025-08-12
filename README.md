# Card Management Service

REST API для управления банковскими картами (Spring Boot 3, Java 17).  
Аутентификация через JWT, роли (ADMIN/USER), маскирование номеров, шифрование PAN, переводы, пагинация и фильтрация.

## Возможности

- **Аутентификация**: Spring Security + JWT, роли `ADMIN` и `USER`.
- **Карты**:
  - Создание / активация / блокировка / удаление (ADMIN)
  - Список своих карт с фильтрацией и пагинацией (USER)
  - Просмотр баланса (USER)
  - Маскирование номера: `**** **** **** 1234` и `last4`
  - (Хук) Шифрование PAN в БД через `CryptoService`
- **Переводы**: между своими картами с валидацией (статус, баланс).
- **БД**: PostgreSQL (dev/prod), H2 (тесты). Liquibase миграции (dev/prod).
- **Документация**: OpenAPI/Swagger (`/swagger-ui`), `docs/openapi.yaml`.
- **Docker**: `docker-compose` для dev БД.
- **Тесты**: Unit-тесты бизнес-логики, H2-конфигурация для тестового профиля.

## Технологический стек

- Java 17, Spring Boot 3.x
- Spring Security, JWT (jjwt 0.11.5)
- Spring Data JPA
- PostgreSQL / H2
- Liquibase
- Gradle
- Docker, Docker Compose

## Безопасность

- JWT с подписью HS256 — секрет задаётся в конфигурации.
- Ролевая модель доступа на уровне методов и эндпоинтов.
- Маскирование номера карты + подключаемый сервис шифрования.

## Структура проекта (основные части)

```
src/main/java/com/example/card_management
├─ controller/        # AuthController, CardController
├─ dto/               # CreateCardRequest, CardDto, TransferRequest, AuthRequest/Response
├─ entity/            # UserEntity, CardEntity, CardStatus
├─ repository/        # UserRepository, CardRepository
├─ security/          # SecurityConfig, JwtTokenProvider, JwtAuthenticationFilter
├─ service/           # AuthService, CardService, CryptoService, UserService
└─ util/              # CardMasker
```

## Запуск (Dev)

1. Запустить БД:
   ```bash
   docker compose up -d
   ```
2. Запустить приложение:
   ```bash
   ./gradlew bootRun
   ```
3. Swagger UI: `http://localhost:8080/swagger-ui`

## Тесты

```bash
./gradlew clean test
```
Тесты используют H2, Liquibase отключён (через `application-test.yml`).

## Процесс аутентификации

1. `POST /api/auth/login` -> `AuthResponse{token}`
2. Использовать заголовок: `Authorization: Bearer <token>`
3. Роли:
   - `ADMIN`: управление пользователями и картами
   - `USER`: просмотр своих карт, запрос блокировки, переводы между своими картами, просмотр баланса

## DTO

- `CreateCardRequest(number, expiry)` — срок действия в формате `YYYY-MM`.
- `CardDto(id, maskedNumber, last4, expiry, status, balance)`.
- `TransferRequest(fromId, toId, amount)`.

## Обработка ошибок

Централизованный обработчик исключений возвращает понятные сообщения, валидация выполняется как на уровне DTO, так и сервисов.

## Безопасность и данные

- Маскирование и `last4` обрабатываются через `CardMasker`.
- Шифрование PAN — через `CryptoService` (можно заменить на AES-GCM или аналог).
- Не хранить секреты в коде — использовать переменные окружения или менеджер секретов.

## Сборка

```bash
./gradlew clean build
```
Готовый jar: `build/libs/*.jar`.

