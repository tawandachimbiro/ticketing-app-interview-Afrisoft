# Step-by-Step: Run the App and See Metrics in Grafana

Follow these steps to build the app, start everything with Docker Compose, and view metrics in a Grafana dashboard.

---

## Prerequisites

- **Docker Desktop** installed and running (includes Docker Compose).
- Ports **3306, 8099, 8080, 9090, 3000, 3100** free (or set different ports in `.env`). (3100 = Loki)

---

## Step 1: Open the project in a terminal

Open PowerShell or Command Prompt and go to the project root:

```powershell
cd "C:\Users\AFROSOFT\IdeaProjects\MyProjects\afrisoft ticketing app\e-commerce-app-customer"
```

---

## Step 2: Create your environment file (first time only)

If you don’t have a `.env` file yet:

```powershell
copy .env.example .env
```

Edit `.env` if you need to change database passwords, JWT secret, or Grafana login. Defaults are fine for local testing.

---

## Step 3: Build and start all services

From the project root, run:

```powershell
docker-compose up -d --build
```

This will:

- Build the Spring Boot app (Maven).
- Start **MariaDB**, **ticketing-app**, **Loki**, **Promtail**, **Prometheus**, **Grafana**, and optionally **phpMyAdmin**.
- Take a few minutes the first time.

Wait until the app is healthy (about 1–2 minutes). Check status:

```powershell
docker-compose ps
```

All services should be “Up”; `ticketing_app` may show “healthy” after a short delay.

---

## Step 4: URLs to test

Use these in your browser or with `curl`:

| What | URL | What to expect |
|------|-----|----------------|
| **App health** | http://localhost:8099/actuator/health | JSON with `"status":"UP"` |
| **Prometheus metrics (raw)** | http://localhost:8099/actuator/prometheus | Plain text with metric lines |
| **Prometheus UI** | http://localhost:9090 | Prometheus UI (Status → Targets to see scrape status) |
| **Grafana** | http://localhost:3000 | Grafana login page |
| **Swagger API docs** | http://localhost:8099/swagger-ui.html | API documentation |
| **phpMyAdmin** (optional) | http://localhost:8080 | Database UI |

**Quick checks:**

```powershell
# Health
curl http://localhost:8099/actuator/health

# Metrics endpoint (first few lines)
curl -s http://localhost:8099/actuator/prometheus | Select-Object -First 20
```

In Prometheus (http://localhost:9090): open **Status → Targets**. The target `ticketing-app` should be **UP**.

---

## Step 5: Log in to Grafana

1. Open **http://localhost:3000**.
2. Log in:
   - **Username:** `admin`
   - **Password:** `admin`  
   (or the values from `.env`: `GRAFANA_ADMIN_USER` / `GRAFANA_ADMIN_PASSWORD`.)
3. If prompted to change password, you can skip or set a new one.

---

## Step 6: See metrics in a Grafana dashboard

Prometheus is already configured as the default data source. To see app metrics you can either **import a ready-made dashboard** or **create a quick panel**.

### Option A: Import a Spring Boot / JVM dashboard (recommended)

1. In Grafana, click **☰ (menu)** → **Dashboards** → **Import**.
2. Enter a dashboard ID and click **Load**:
   - **4701** – JVM (Micrometer) – good for JVM memory, threads, GC.
   - **11378** – Spring Boot 2.1 Statistics – HTTP, JVM, logback.
3. Choose **Prometheus** as the data source, then **Import**.
4. You should see panels for JVM memory, HTTP requests, etc. If the app was just started, wait 1–2 minutes and refresh; data appears after Prometheus has scraped a few times.

### Option B: Create one panel manually

1. Click **☰** → **Dashboards** → **New** → **New dashboard**.
2. **Add visualization**.
3. In the query editor, select **Prometheus** and try:
   - `jvm_memory_used_bytes` – JVM memory usage.
   - `http_server_requests_seconds_count` – HTTP request count.
4. Click **Run query**, then **Apply** or **Save**. Save the dashboard with a name (e.g. “Ticketing app metrics”).

---

## Step 7: Generate some traffic (optional)

To see HTTP and JVM metrics change:

- Open **http://localhost:8099/swagger-ui.html** and call a few endpoints (e.g. GET events, health).
- Or: `curl http://localhost:8099/actuator/health` a few times.

Wait ~15–30 seconds, then refresh your Grafana dashboard; request counts and similar metrics should update.

---

## Summary: URLs to test and see metrics

| Step | URL | Purpose |
|------|-----|--------|
| Test app | http://localhost:8099/actuator/health | Confirm app is up |
| Test metrics endpoint | http://localhost:8099/actuator/prometheus | Raw Prometheus metrics from the app |
| Check Prometheus | http://localhost:9090 → Status → Targets | Confirm scrape is UP |
| **See metrics in Grafana** | **http://localhost:3000** | Log in (admin/admin) → Import dashboard **4701** or **11378** → view JVM/HTTP metrics |

---

## Stopping everything

```powershell
docker-compose down
```

To remove data (DB, Prometheus, Grafana) as well:

```powershell
docker-compose down -v
```

---

## Troubleshooting: "Ports are not available" (8099 in use)

If you see:

```text
Error response from daemon: Ports are not available: exposing port TCP 0.0.0.0:8099 ... bind: Only one usage of each socket address ...
```

then **port 8099 is already in use**. The app container fails to start, so Prometheus and Grafana may never start or the stack is left in a bad state.

**Fix (choose one):**

### Option A – Use a different app port (quickest)

1. Open `.env` and set a different host port for the app, e.g.:
   ```env
   APP_PORT=8100
   ```
2. Clean up and start again:
   ```powershell
   docker-compose down
   docker-compose up -d
   ```
3. Use **http://localhost:8100** for the app (health, Swagger, etc.). Prometheus and Grafana stay at **http://localhost:9090** and **http://localhost:3000**.

### Option B – Free port 8099

1. See what is using 8099:
   ```powershell
   netstat -ano | findstr :8099
   ```
2. Stop any old containers:
   ```powershell
   docker-compose down
   docker ps -a
   ```
   If you see a `ticketing_app` (or similar) container, stop it: `docker stop <container_id>`.
3. If a non-Docker process uses 8099, close that app or change its port.
4. Start again:
   ```powershell
   docker-compose up -d
   ```

---

## Start Docker stack (with Loki)

The stack includes **Loki** (logs) and **Promtail** (sends container logs to Loki). Grafana is provisioned with **Prometheus** and **Loki** as data sources so Spring Boot Observability dashboards can use both.

### Step-by-step

1. **Open terminal** in the project root:
   ```cmd
   cd "C:\Users\AFROSOFT\IdeaProjects\MyProjects\afrisoft ticketing app\e-commerce-app-customer"
   ```

2. **Optional:** Copy and edit `.env` (e.g. set `LOKI_PORT=3100` if 3100 is in use).

3. **Stop any existing stack:**
   ```cmd
   docker-compose down
   ```

4. **Start the full stack (including Loki):**
   ```cmd
   docker-compose up -d --build
   ```

5. **Wait 2–3 minutes**, then check:
   ```cmd
   docker-compose ps
   ```
   All services should be **Up** (mariadb, ticketing-app, loki, promtail, prometheus, grafana, phpmyadmin).  
   **Note:** On Windows, if **Promtail** fails (e.g. path `/var/lib/docker/containers` not found), you can comment out the `promtail` service and the `loki` + Grafana Loki datasource still work; you just won’t have container logs in Loki until you use another method (e.g. Loki Docker plugin).

6. **Open Grafana:** http://localhost:3000 (admin / admin).  
   In **Connections → Data sources** you should see **Prometheus** (default) and **Loki**.  
   Use **Explore**, choose **Loki**, and try a query like `{container=~"ticketing_app.*"}` to see app logs.
