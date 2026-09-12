# Deploy CampusCredit on Render

This archive contains only the Spring Boot backend. It is intentionally separate from the React frontend.

## Before deploying

Create these services in the same Render region:

1. **PostgreSQL** — required.
2. **Key Value / Redis-compatible service** — required for idempotency, balance caching, and rate limiting.
3. **RabbitMQ broker** — required for transfer notifications.

## Deploy the API

Create a Render **Web Service** from this backend source using the included `Dockerfile`. In its Environment page, add the variables from `.env.example`, replacing every `YOUR_...` value.

For the Postgres JDBC URL, use this exact pattern:

```text
jdbc:postgresql://RENDER_DATABASE_HOST:5432/campus_credit_db
```

Do not use `localhost` on Render.

## Frontend connection

When the frontend is deployed, replace `CORS_ALLOWED_ORIGINS` with its exact public URL, for example:

```text
CORS_ALLOWED_ORIGINS=https://your-frontend.pages.dev
```

Keep `COOKIE_SECURE=true` and `COOKIE_SAME_SITE=None` when the frontend and API are on different HTTPS domains. Redeploy the API after changing these variables.

## Security

Do not reuse the password or JWT values that were previously shared in chat or included in older configuration. Use new Render credentials and a new random `JWT_SECRET`.
