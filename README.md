# mail-service

A small, general-purpose Spring Boot service for sending emails over SMTP.

## Endpoints

OpenAPI / Swagger UI is available at `/swagger-ui.html`, the spec at `/v3/api-docs`.

### `POST /api/contact` (public)

Sends a contact-form submission to the fixed recipient `mail@fynn-koch.de` (hard-coded
in `MailService`). The recipient cannot be set by the caller, so this endpoint cannot be
misused as an open relay. The sender's address is set as `Reply-To`.

```json
{
  "name": "Maria Muster",
  "email": "maria@example.com",
  "organisation": "Example GmbH",
  "phone": "0123 456789",
  "message": "Eine Nachricht."
}
```

`organisation` and `phone` are optional. Returns `200 OK` on success.

## Configuration

All settings are environment variables (see `src/main/resources/application.yml`):

| Variable | Purpose | Default |
| --- | --- | --- |
| `MAIL_HOST` | SMTP host | `smtp.example.com` |
| `MAIL_PORT` | SMTP port | `465` |
| `MAIL_USERNAME` | SMTP user / `From` address | `noreply@example.com` |
| `MAIL_PASSWORD` | SMTP password | `changeme` |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed origins | `http://localhost:4200` |

## Local development

```bash
./gradlew bootRun
./gradlew test
./gradlew bootJar
```

Swagger UI: http://localhost:8080/swagger-ui.html

## Deployment

The service is packaged as a Docker image and exposes no host ports; it listens on
port `8080` inside the container and is meant to sit behind a reverse proxy.

```bash
docker compose -f docker-compose-mail-service.yml up --build -d
```

### Jenkins

`Jenkinsfile` builds and deploys via docker compose. It expects these Jenkins
credentials: `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`.
