# Документация API

Полное описание находится в файле `openapi.yaml`. Или же в Swagger-UI по конечной точке `localhost:8080/swagger-ui.html` при запуске приложения.

### Конечные точки (endpoints):
#### Authentication
- `POST /auth/register`: регистрация в системе
- `POST /auth/login`: вход в систему
- `POST /auth/refresh-token`: получение новой пары токенов (access, refresh)
#### Admin Cards
- `GET /admin/cards/{id}`: получение банковской карты по ID
- `DELETE /admin/cards/{id}`: удаление банковской карты из системы по ID
- `PATCH /admin/cards/{id}/status`: изменение статуса банковской карты (ACTIVE, BLOCKED) по ID
- `POST /admin/cards/create/{userId}`: создание банковской карты для определенного пользователя
#### Admin Users
- `GET /admin/users/{id}`: получение данных о пользователе по ID
- `PATCH /admin/users/{id}/role`: изменение роли пользователя с `USER` на `ADMIN` по ID
- `PATCH /admin/users/{id}/username`: изменение имени пользователя по ID
- `GET /admin/users/{userId}/cards`: получение списка банковских карт пользователя с учетом пагинации, сортировки и фильтрации по различным полям
#### Cards
- `GET /cards/{id}`: получение информации о банковской карте по ID
- `GET /cards/{id}/balance`: получение баланса банковской карты по ID
- `GET /cards/{id}/card-number`: получение номера банковской карты (без маски) по ID
- `PATCH /cards/block/{id}`: запрос на блокировку банковской карты по ID
- `GET /cards/cards`: получение списка банковских карт (ID пользователя определяется из его JWT-токена)
#### Transactions
- `PATCH /transactions/transfers`: осуществление перевода денег между банковскими картами одного пользователя
