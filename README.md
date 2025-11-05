
This document provides instructions for running and interacting with the microservices-based application. The project is containerized with Docker and orchestrated using Docker Compose, with Traefik acting as the reverse proxy and API gateway.

## Architecture Overview

The system consists of the following services:

- **Traefik** – Handles incoming requests, routes traffic to services, provides a monitoring dashboard, and manages API gateway functions such as JWT validation.
    
- **Frontend** – A containerized web interface.
    
- **Auth Service** – Manages user authentication, registration, and JWT issuance.
    
- **User Service** – Handles user data, profiles, and role-based access.
    
- **Device Service** – Manages devices associated with users.
    
- **PostgreSQL Databases** – Three independent databases, one per backend service.
    

## Setup and Deployment

### Prerequisites

- Docker
    
- Docker Compose
    

### Create the External Network

Before starting the services, create the shared Docker network:

```bash
docker network create demo_net
```

### Start the Application

Run all services from the project root directory:

```bash
docker-compose up -d
```

This builds and starts all containers in detached mode.

### Access Points

- Frontend: `http://localhost`
    
- Traefik Dashboard: `http://localhost:8080`
    

## Authentication and Authorization

### Authentication Flow

1. The user logs in via the frontend.
    
2. The frontend sends credentials to the Auth Service (`POST /auth/login`).
    
3. The Auth Service returns a JSON Web Token (JWT).
    
4. The frontend stores the token in the browser’s local storage.
    
5. Subsequent requests include the token in the `Authorization` header.
    
6. Traefik validates the token and forwards authenticated requests to the backend services.
    

### Roles

- **ROLE_USER:** Access to personal data and devices.
    
- **ROLE_ADMIN:** Access to all users, devices, and administrative endpoints.
    

## API Documentation

Swagger UI is available for each backend service:

- Auth Service: `http://localhost/auth/swagger-ui/index.html`
    
- User Service: `http://localhost/users/swagger-ui/index.html`
    
- Device Service: `http://localhost/devices/swagger-ui/index.html`

For testing the APIs, the same `http://localhost/` root path can be used.