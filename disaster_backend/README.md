# Disaster Response Coordination API

## Run

Start MongoDB locally, then run:

```powershell
./mvnw.cmd spring-boot:run
```

Optional environment variables: `MONGODB_URI`, `JWT_SECRET` (at least 32 bytes), `JWT_EXPIRATION_MS`, and `UPLOAD_DIRECTORY`.

To provision the first coordinator, set `BOOTSTRAP_COORDINATOR_USERNAME` and `BOOTSTRAP_COORDINATOR_PASSWORD` (minimum eight characters) before the first startup. The bootstrap account is created only when its username does not already exist. Regular `/signup` accounts always receive the `VOLUNTEER` role.

## Authentication

`POST /signup` and `POST /login` accept `{"username":"...","password":"..."}`. They keep the original plain-text responses (`Signup Successful`, a role, or an error message) for frontend compatibility. Login also includes an optional JWT in the `X-Auth-Token` response header.

Public: `GET /hello`, `GET /reports`, `GET /report/search/location?location=...`, `GET /report/search/type?disasterType=...`, and `/uploads/**`.

All endpoints are currently open for compatibility with the existing frontend. The backend still accepts a JWT in the `Authorization: Bearer <token>` header, but does not require one until the frontend is updated to send it.

Reports accept JPEG, PNG, or WebP images up to 5 MB. Images are saved below the configured upload directory and served as `/uploads/<generated-name>`.

## Verify

```powershell
./mvnw.cmd "-Dmaven.repo.local=.m2" test
```
