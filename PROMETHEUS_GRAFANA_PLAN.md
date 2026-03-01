# Plan: Add Prometheus and Grafana for Monitoring

This document describes a step-by-step plan to add Prometheus and Grafana to the e-commerce ticketing app for metrics collection, storage, and visualization. **Do not implement until you have given the go-ahead.**

---

## Current State (Summary)

| Item | Details |
|------|--------|
| **App** | Spring Boot 3.2.8, Java 17, multi-module Maven (domain, persistence, service, api, app) |
| **Run** | JAR from `app` module; Docker via `docker-compose` (services: `mariadb`, `ticketing-app`, `phpmyadmin`) |
| **Actuator** | Already present: exposes `health`, `info`, `metrics` at `/actuator`. Health is public; `info` and `metrics` require ADMIN role. |
| **Ports** | App: 8099, MariaDB: 3306, phpMyAdmin: 8080 |

The app does **not** yet expose metrics in Prometheus format, and there are no Prometheus or Grafana services.

---

## Why Add Prometheus and Grafana?

1. **Prometheus** – Industry-standard metrics backend: scrapes HTTP endpoints (e.g. `/actuator/prometheus`), stores time-series data, and supports querying (PromQL) and alerting.
2. **Grafana** – Connects to Prometheus (and others), provides dashboards and visualizations for JVM, HTTP, DB pool, cache, and custom business metrics.
3. **Outcome** – You get visibility into JVM memory/threads, HTTP request rates/latencies, Hikari pool usage, cache stats, and (optionally) custom counters/timers for ticket purchases, payments, etc., with minimal code change by reusing Spring Boot’s built-in metrics.

---

## High-Level Steps

1. Expose Prometheus-format metrics from the Spring Boot app (Micrometer + actuator).
2. Allow Prometheus to scrape that endpoint (security: who can call `/actuator/prometheus`).
3. Run Prometheus in Docker and configure it to scrape the app.
4. Run Grafana in Docker and add Prometheus as a data source.
5. (Optional) Add a ready-made or custom Grafana dashboard for the app.

---

## Step-by-Step Plan

### Step 1: Add Micrometer Prometheus dependency (Maven)

**What:** Add `micrometer-registry-prometheus` to the **root** `pom.xml` (so all modules that use actuator get the registry).

**Why:** Spring Boot Actuator already uses Micrometer for `metrics`. This dependency registers a Prometheus `MeterRegistry` and exposes a `/actuator/prometheus` endpoint that returns metrics in Prometheus text format. No extra application code is required for standard JVM/HTTP/DB/cache metrics.

**Where:** `pom.xml` (next to `spring-boot-starter-actuator`).

---

### Step 2: Expose the `prometheus` actuator endpoint (configuration)

**What:** In `application-prod.yml` (and optionally `application-dev.yml` / `application-staging.yml` if you want monitoring there too), add `prometheus` to:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

**Why:** By default, only `health` is exposed. Adding `prometheus` makes the `/actuator/prometheus` endpoint available so Prometheus can scrape it.

**Where:** `app/src/main/resources/application-prod.yml` (and dev/staging if desired).

---

### Step 3: Allow unauthenticated access to `/actuator/prometheus` (security)

**What:** In `SecurityConfig.java`, add a rule so that `/actuator/prometheus` is allowed without authentication (e.g. `permitAll()`), **before** the rule that restricts `/actuator/**` to ADMIN.

**Why:** Prometheus scrapes via HTTP GET and does not send JWT tokens. So either:
- (A) Expose `/actuator/prometheus` without auth and rely on network isolation (Prometheus and app on same Docker network, not exposed to the public), or  
- (B) Use a separate management port and firewall it.

We choose (A) for simplicity: only the Prometheus container will reach the app on the internal network; the host port for the app remains as today. If you later expose the app to the internet, you can lock down `/actuator/prometheus` (e.g. management port or IP allowlist).

**Where:** `app/src/main/java/com/changamire/configs/SecurityConfig.java` – add:

`.requestMatchers("/actuator/prometheus").permitAll()`  
before  
`.requestMatchers("/actuator/**").hasRole("ADMIN")`

---

### Step 4: Create Prometheus configuration file

**What:** Add a `prometheus.yml` in `app/src/main/resources` (with the other YAML configs) that:
- Defines a scrape job for the Spring Boot app.
- Targets the app using the Docker service name and port, e.g. `http://ticketing-app:8099/actuator/prometheus`.
- Uses a reasonable scrape interval (e.g. 15s).

**Why:** Prometheus needs to know which URL to scrape and how often. Using the Docker service name ensures it works from inside the `ticketing-network` without depending on host ports.

**Where:** New file `app/src/main/resources/prometheus.yml` (and reference it from `docker-compose`).

---

### Step 5: Add Prometheus service to Docker Compose

**What:** In `docker-compose.yml`:
- Add a `prometheus` service (official `prometheus/prometheus` image).
- Mount the `prometheus.yml` from Step 4 as the config file.
- Attach it to the same network as the app (`ticketing-network`).
- Expose a host port (e.g. 9090) so you can open the Prometheus UI and run queries.

**Why:** Prometheus runs as a separate container, scrapes the app periodically, and stores metrics. The UI is useful to verify targets and run ad-hoc PromQL queries.

**Where:** `docker-compose.yml`.

---

### Step 6: Add Grafana service to Docker Compose

**What:** In `docker-compose.yml`:
- Add a `grafana` service (official `grafana/grafana` image).
- Set admin user/password via environment (or use defaults for dev; document for prod).
- Attach to `ticketing-network`.
- Expose a host port (e.g. 3000).
- Optionally add a volume for Grafana data so dashboards and data sources persist across restarts.

**Why:** Grafana will connect to Prometheus (using the service name `prometheus` and port 9090) and provide dashboards.

**Where:** `docker-compose.yml`.

---

### Step 7: Configure Grafana to use Prometheus (provisioning or manual)

**What:** Either:
- **Option A (recommended for reproducibility):** Add Grafana provisioning (YAML) to automatically add Prometheus as a data source on startup (e.g. `datasources/datasources.yml` and a volume mount).
- **Option B:** Document that after first login the user must add a data source: type Prometheus, URL `http://prometheus:9090`.

**Why:** So Grafana can query Prometheus without manual setup every time.

**Where:** New folder e.g. `grafana/provisioning/datasources/` and `docker-compose` volume mount, or docs only.

---

### Step 8 (Optional): Add a pre-built or custom Grafana dashboard

**What:** Import a public Spring Boot 2 / Micrometer dashboard (e.g. from Grafana.com) or create a minimal dashboard with panels for:
- JVM memory (heap, non-heap),
- HTTP request count and latency,
- Hikari connection pool,
- Optional: custom metrics if we add them later.

**Why:** Gives immediate value without building panels from scratch.

**Where:** Either provisioned JSON in `grafana/provisioning/dashboards/` or document “Import dashboard ID XXXXX”.

---

### Step 9: Document ports and usage

**What:** Update `.env.example` and any docs (e.g. `DOCKER_SETUP.md` or README) with:
- `PROMETHEUS_PORT=9090` (optional in `.env` if you use it in compose).
- `GRAFANA_PORT=3000`.
- How to open Prometheus (http://localhost:9090) and Grafana (http://localhost:3000), and how to add the Prometheus data source if not provisioned.

**Why:** So anyone running the stack knows how to access monitoring.

---

## File Change Summary (for implementation phase)

| # | Action | File(s) |
|---|--------|--------|
| 1 | Add dependency | `pom.xml` |
| 2 | Expose prometheus endpoint | `app/src/main/resources/application-prod.yml` (and optionally dev/staging) |
| 3 | Allow unauthenticated /actuator/prometheus | `app/src/main/java/com/changamire/configs/SecurityConfig.java` |
| 4 | Add scrape config | New: `app/src/main/resources/prometheus.yml` |
| 5 | Add Prometheus service | `docker-compose.yml` |
| 6 | Add Grafana service + volume | `docker-compose.yml` |
| 7 | Provision Prometheus data source | New: `grafana/provisioning/datasources/datasources.yml` + volume in compose |
| 8 | (Optional) Dashboard | New: dashboard JSON or docs |
| 9 | Document ports/usage | `.env.example`, `DOCKER_SETUP.md` or README |

---

## Order of implementation

1. **App changes (build + config + security):** Step 1 → Step 2 → Step 3. Then run the app and confirm `GET http://localhost:8099/actuator/prometheus` returns Prometheus text format (without auth when tested locally).
2. **Prometheus:** Step 4 → Step 5. Start compose, check Prometheus UI → Status → Targets and confirm the app target is UP.
3. **Grafana:** Step 6 → Step 7. Log in to Grafana, confirm Prometheus data source works (e.g. run a simple query).
4. **Polish:** Step 8 (optional), Step 9.

---

## Risks and considerations

- **Security:** `/actuator/prometheus` will be unauthenticated. Mitigation: use only on internal Docker network; do not expose the app’s management path to the public without further hardening (e.g. management port, reverse proxy, or IP allowlist).
- **Resource usage:** Prometheus and Grafana add two containers and some disk for metrics; for a single app this is usually negligible.
- **Persistence:** Add a volume for Prometheus data if you want metrics to survive container restarts; same for Grafana if you create dashboards you want to keep.

---

## Go-ahead

After you review this plan and give the go-ahead, implementation will follow the steps above in the order listed, with code and config changes applied in the repo.
