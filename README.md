# mail-service

A small, general-purpose Spring Boot service for sending emails over SMTP.

## Endpoints

OpenAPI / Swagger UI is available at `/swagger-ui.html`, the spec at `/v3/api-docs`.

### `POST /api/contact` (public)

Sends a contact-form submission to the server-configured recipient
(`mail.contact-recipient`, default `mail@fynn-koch.de`). The recipient is fixed
server-side, so this endpoint cannot be misused as an open relay. The sender's address
is set as `Reply-To`.

```json
{
  "name": "Maria Muster",
  "email": "maria@tanzschule-muster.de",
  "organisation": "Tanzschule Muster",
  "phone": "0123 456789",
  "message": "Wir hätten gern ein Mockup."
}
```

`organisation` and `phone` are optional. Returns `200 OK` on success.

### `POST /api/mail` (API key required)

Sends a generic email to an arbitrary recipient. Requires the `X-Api-Key` header to
match `mail.api-key`. If no API key is configured the endpoint is disabled and always
returns `401`.

```
X-Api-Key: <shared-secret>
```
```json
{
  "to": "someone@example.com",
  "subject": "Betreff",
  "body": "Nachrichtentext",
  "replyTo": "optional@example.com"
}
```

## Configuration

All settings are environment variables (see `src/main/resources/application.yml`):

| Variable | Purpose | Default |
| --- | --- | --- |
| `MAIL_HOST` | SMTP host | `smtp.example.com` |
| `MAIL_PORT` | SMTP port | `465` |
| `MAIL_USERNAME` | SMTP user / `From` address | `noreply@example.com` |
| `MAIL_PASSWORD` | SMTP password | `changeme` |
| `CONTACT_RECIPIENT` | Fixed recipient for `/api/contact` | `mail@fynn-koch.de` |
| `MAIL_API_KEY` | Shared secret for `/api/mail`; empty disables it | _(empty)_ |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed origins | `http://localhost:4200` |

## Local development

```bash
./gradlew bootRun
./gradlew test
./gradlew bootJar
```

Swagger UI: http://localhost:8080/swagger-ui.html

## Deployment

The service runs as a container on the shared `proxy-net` Docker network and exposes
no host ports. The central reverse proxy in `fynn-koch-landingpage` terminates TLS and
routes public traffic to it.

```bash
docker compose -f docker-compose-mail-service.yml up --build -d
```

### Wiring it into the reverse proxy

`mail-service` does not run its own nginx. To make the API reachable from the browser,
add a `location /api/` block to the appropriate server block in
`fynn-koch-landingpage/nginx.conf`. A ready-to-paste example is in [`nginx.conf`](./nginx.conf).
Exposing the API on the same origin as the landing page avoids CORS entirely.

### Jenkins

`Jenkinsfile` deploys via docker compose. It expects these Jenkins credentials:
`MAIL_SERVICE_MAIL_HOST`, `MAIL_SERVICE_MAIL_PORT`, `MAIL_SERVICE_MAIL_USERNAME`,
`MAIL_SERVICE_MAIL_PASSWORD`, `MAIL_SERVICE_CONTACT_RECIPIENT`, `MAIL_SERVICE_API_KEY`.
