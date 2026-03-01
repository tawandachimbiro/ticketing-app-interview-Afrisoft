# Docker Setup Guide for Ticketing Application

This guide explains how to run the ticketing application using Docker.

## Prerequisites

- Docker Desktop installed and running
- Docker Compose installed (comes with Docker Desktop)
- At least 4GB of available RAM
- Ports 3306, 8099, 8080, 9090, and 3000 available (9090 and 3000 for Prometheus and Grafana)

## Project Structure

```
e-commerce-app-customer/
├── Dockerfile              # Multi-stage Docker build
├── docker-compose.yml      # Container orchestration
├── app/src/main/resources/
│   ├── prometheus.yml      # Prometheus scrape config for the app
│   ├── application-*.yml   # Spring config (dev, staging, prod)
│   └── ...
├── grafana/
│   └── provisioning/
│       └── datasources/   # Grafana datasource (Prometheus) auto-provisioned
├── .dockerignore          # Files to exclude from build
├── .env.example           # Environment variables template
├── init.sql              # Database initialization
└── DOCKER_SETUP.md       # This file
```

## Quick Start

### 1. Create Environment File

Copy the example environment file and update with your values:

```bash
cp .env.example .env
```

Edit `.env` and update sensitive values like passwords and API keys.

### 2. Build and Start Services

```bash
# Build and start all services
docker-compose up -d --build

# View logs
docker-compose logs -f ticketing-app

# Check service status
docker-compose ps
```

### 3. Access the Application

- **Application API**: http://localhost:8099
- **API Documentation**: http://localhost:8099/swagger-ui.html
- **Health Check**: http://localhost:8099/actuator/health
- **phpMyAdmin**: http://localhost:8080
- **Prometheus** (metrics): http://localhost:9090
- **Grafana** (dashboards): http://localhost:3000 (default login: admin / admin)

## Services

### MariaDB Database
- **Container**: `ticketing_mariadb`
- **Port**: 3306
- **Database**: ticketing_app
- **User**: ticketing_user
- **Data Volume**: `mariadb_data` (persistent)

### Spring Boot Application
- **Container**: `ticketing_app`
- **Port**: 8099
- **Health Check**: Enabled with 30s interval
- **QR Codes**: Stored in `qrcodes` volume

### phpMyAdmin (Optional)
- **Container**: `ticketing_phpmyadmin`
- **Port**: 8080
- **Purpose**: Database management UI

### Prometheus (Monitoring)
- **Container**: `ticketing_prometheus`
- **Port**: 9090 (override with `PROMETHEUS_PORT` in `.env`)
- **Purpose**: Scrapes `/actuator/prometheus` from the app every 15s; stores metrics for querying (PromQL) and Grafana.
- **Config**: `app/src/main/resources/prometheus.yml`.

### Grafana (Monitoring)
- **Container**: `ticketing_grafana`
- **Port**: 3000 (override with `GRAFANA_PORT` in `.env`)
- **Purpose**: Dashboards and visualizations; Prometheus is pre-configured as the default data source.
- **Login**: Set via `GRAFANA_ADMIN_USER` and `GRAFANA_ADMIN_PASSWORD` (default: admin / admin). Change in production.

## Docker Commands

### Basic Operations

```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# Restart services
docker-compose restart

# View logs
docker-compose logs -f [service-name]

# Stop and remove everything (including volumes)
docker-compose down -v
```

### Rebuild Application

```bash
# Rebuild only the application
docker-compose up -d --build ticketing-app

# Force rebuild without cache
docker-compose build --no-cache ticketing-app
```

### Database Operations

```bash
# Access MariaDB shell
docker exec -it ticketing_mariadb mysql -u root -p

# Backup database
docker exec ticketing_mariadb mysqldump -u root -pAmill2017 ticketing_app > backup.sql

# Restore database
docker exec -i ticketing_mariadb mysql -u root -pAmill2017 ticketing_app < backup.sql
```

### Troubleshooting

```bash
# Check container status
docker-compose ps

# View container logs
docker-compose logs ticketing-app
docker-compose logs mariadb

# Access container shell
docker exec -it ticketing_app sh

# Check application health
curl http://localhost:8099/actuator/health

# Restart specific service
docker-compose restart ticketing-app
```

## Environment Variables

Key environment variables (see `.env.example` for full list):

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILE` | Active Spring profile | prod |
| `DB_NAME` | Database name | ticketing_app |
| `DB_USER` | Database user | ticketing_user |
| `DB_PASSWORD` | Database password | - |
| `APP_PORT` | Application port | 8099 |
| `JWT_SECRET` | JWT signing secret | - |
| `MAIL_USERNAME` | Email username | - |
| `MAIL_PASSWORD` | Email app password | - |
| `PROMETHEUS_PORT` | Prometheus UI port | 9090 |
| `GRAFANA_PORT` | Grafana UI port | 3000 |
| `GRAFANA_ADMIN_USER` | Grafana admin username | admin |
| `GRAFANA_ADMIN_PASSWORD` | Grafana admin password | admin |

## Production Deployment

### Security Recommendations

1. **Change Default Passwords**: Update all passwords in `.env`
2. **Secure JWT Secret**: Generate a strong JWT secret
3. **Mail Credentials**: Use app-specific passwords
4. **Remove phpMyAdmin**: Comment out in production
5. **Enable SSL**: Configure reverse proxy (nginx/traefik)
6. **Restrict Ports**: Only expose necessary ports

### Resource Limits

Add resource limits to `docker-compose.yml`:

```yaml
services:
  ticketing-app:
    deploy:
      resources:
        limits:
          cpus: '2'
          memory: 2G
        reservations:
          cpus: '1'
          memory: 1G
```

### Backup Strategy

```bash
# Automated backup script
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
docker exec ticketing_mariadb mysqldump -u root -p$DB_ROOT_PASSWORD ticketing_app > backup_$DATE.sql
```

## Multi-Environment Setup

### Development
```bash
SPRING_PROFILE=dev docker-compose up -d
```

### Staging
```bash
SPRING_PROFILE=staging docker-compose up -d
```

### Production
```bash
SPRING_PROFILE=prod docker-compose up -d
```

## Monitoring

### Prometheus and Grafana

The stack includes Prometheus and Grafana for metrics and dashboards:

1. **Prometheus** (http://localhost:9090): Check **Status → Targets** to confirm the `ticketing-app` target is UP. Use the **Graph** tab to run PromQL queries (e.g. `jvm_memory_used_bytes`, `http_server_requests_seconds_count`).
2. **Grafana** (http://localhost:3000): Log in with the admin credentials from `.env`. The **Prometheus** data source is already provisioned. You can create dashboards or import existing ones (e.g. “JVM (Micrometer)” or “Spring Boot 2.1 Statistics” from Grafana.com) to visualize JVM, HTTP, and Hikari metrics from the app.
3. **Metrics endpoint**: The app exposes Prometheus-format metrics at http://localhost:8099/actuator/prometheus (no auth; intended for scrape by Prometheus on the Docker network).

### Health Checks

Both services have health checks configured:
- **Database**: Checks MariaDB initialization
- **Application**: Checks Spring Boot actuator endpoint

```bash
# View health status
docker inspect ticketing_app | grep -A 10 Health
```

### Logs

```bash
# Follow all logs
docker-compose logs -f

# Follow specific service
docker-compose logs -f ticketing-app

# View last 100 lines
docker-compose logs --tail=100 ticketing-app
```

## Common Issues

### Port Already in Use
```bash
# Find process using port
netstat -ano | findstr :8099

# Change port in .env
APP_PORT=8100
```

### Database Connection Failed
- Check if MariaDB is healthy: `docker-compose ps`
- Wait for health check to pass (30-60 seconds)
- Check database logs: `docker-compose logs mariadb`

### Application Won't Start
- Check logs: `docker-compose logs ticketing-app`
- Verify environment variables in `.env`
- Ensure database is healthy before app starts

## Clean Up

```bash
# Stop and remove containers
docker-compose down

# Remove volumes (WARNING: deletes all data)
docker-compose down -v

# Remove images
docker rmi $(docker images -q e-commerce-app-customer*)

# Complete cleanup
docker system prune -a --volumes
```

## Support

For issues or questions:
1. Check logs: `docker-compose logs -f`
2. Verify environment variables
3. Check Docker resources (CPU/Memory)
4. Review application health: http://localhost:8099/actuator/health
