# MonthlyChallenge App

A social habit-building and challenge-tracking platform with streaks, partial-completion tracking, and friend-based accountability.

## Tech Stack

- **Backend**: Java 21, Spring Boot 3, Hexagonal Architecture
- **Frontend**: React 18, TypeScript, Vite
- **Database**: PostgreSQL 16
- **Auth**: Keycloak 24
- **Build**: Maven (backend), npm (frontend)

## Project Structure

```
monthly-challenge/
├── backend/                    # Spring Boot — Hexagonal Architecture
│   └── src/main/java/com/monthlychallenge/
│       ├── domain/             # Core domain — entities, value objects, domain services
│       ├── application/        # Use cases + inbound/outbound ports
│       └── infrastructure/     # Adapters — REST, JPA, Keycloak, Scheduler, Notifications
├── frontend/                   # React 18 + TypeScript (Vite)
└── docker/                     # Docker Compose for local dev
```

## Getting Started

### Prerequisites
- **Java 21** (required — Lombok does not support Java 25 yet)
- Node 20+
- Docker & Docker Compose

> **macOS with multiple JDKs:** Set Java 21 before building:
> ```bash
> export JAVA_HOME=/opt/homebrew/opt/openjdk@21
> export PATH=$JAVA_HOME/bin:$PATH
> ```

### Run locally

```bash
# 1. Start infrastructure (PostgreSQL + Keycloak)
cd docker
docker-compose up -d

# 2. Backend (must use Java 21)
cd backend
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
./mvnw spring-boot:run

# 3. Frontend
cd frontend
npm install && npm run dev
```

## Keycloak Setup
- Admin console: http://localhost:8180
- Realm: `monthly-challenge`
- Client ID: `monthly-challenge-app`
- Default admin: `admin / admin`

## API Base URL
- Backend: http://localhost:8080/api
- Keycloak: http://localhost:8180
