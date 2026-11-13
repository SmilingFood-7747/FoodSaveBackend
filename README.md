# FoodSave Backend

REST API implemented with Java 21, Spring Boot, PostgreSQL, and Flyway. The code follows the bounded contexts and supporting Shared area already defined in the FoodSave report: IAM, Offer Management, Reservation & Pickup, Business Management, Notifications, Feedback, and Shared. It does not introduce additional domain contexts.

## Run locally

```powershell
docker compose up -d postgres
.\mvnw.cmd spring-boot:run
```

The API listens on `http://localhost:8080`. OpenAPI documentation is available at `http://localhost:8080/swagger-ui.html`; health check at `/health`.

Local PostgreSQL defaults are in `application-local.properties`. Deployment settings are supplied with environment variables. Set `FOODSAVE_AUTH_SECRET` to a strong secret of at least 32 bytes and configure `CORS_ALLOWED_ORIGINS` for the deployed frontend.

## Implemented API

Public reads:

- `GET /offers`, `GET /offers/{id}` and equivalent `/api/v1` routes.
- `GET /businesses`, `GET /businesses/{id}` and equivalent `/api/v1` routes.

IAM:

- `POST /auth/signup` accepts `fullName`, `email`, `phone`, `role` (`CUSTOMER` or `BUSINESS_OWNER`), and `password`.
- `POST /auth/signin` returns an access token and public account fields. Passwords use PBKDF2; hashes are never returned.
- `GET/PATCH /api/users/{id}/profile` (also `/auth/users/{id}/profile`) requires the account's bearer token (or ADMIN).

Business Management and Offer Management:

- `POST /businesses`, `GET /businesses/mine`, and `PUT /businesses/{id}/profile` require a business owner token.
- `GET /businesses/{id}/summary` returns offer, reservation, pickup, cancellation, and sales totals for the owner.
- `POST /offers`, `PUT /offers/{id}`, `PATCH /offers/{id}/pause`, and `DELETE /offers/{id}` require ownership of the business. Write routes also have `/api/v1` aliases.
- An expiration scheduler marks active offers expired after their deadline.

Reservation & Pickup:

- `POST /reservations` uses the authenticated customer identity, reserves available units transactionally, and returns a pickup code.
- `GET /reservations` and `GET /reservations/{id}` show the authenticated customer's reservations.
- `GET /reservations/business/{businessId}` shows reservations to that business's owner.
- `PATCH /reservations/{id}/confirm` accepts `{ "code": "..." }` and confirms pickup once.
- `PATCH /reservations/{id}/cancel` cancels an eligible reservation and restores its units.

Notifications:

- `GET /notifications`, `PATCH /notifications/{id}/read`, `GET /notifications/preferences`, and `PUT /notifications/preferences` are scoped to the authenticated account.
- Reservation confirmation, new reservation, cancellation, and pickup reminder messages are persisted in-app. Pickup reminders honor user preferences.

Feedback:

- `GET /feedback/reviews` and `POST /feedback/reviews` expose reviews; a review can only be submitted by the customer after pickup, once per reservation.
- `GET /feedback/requests` and `POST /feedback/requests` let an authenticated user view and create support requests.

All authenticated routes accept `Authorization: Bearer <accessToken>`. Responses use the frontend's camelCase resource fields where they overlap. Error responses use `ProblemDetail` and support English or Latin American Spanish through `Accept-Language`.

## Database

Flyway migrations create the catalog, reservation, IAM, notification, and feedback tables and load the current offer/business examples. PostgreSQL is the deployed persistence store; the Fake API remains a separate read-only demo data source until the frontend is switched to this API and authenticated writes are integrated.

## Deployment

`Dockerfile` and `render.yaml` define a Render Web Service and PostgreSQL database. Render's free database has limited retention and the free web service may spin down, so this deployment configuration is suitable for a coursework preview.
