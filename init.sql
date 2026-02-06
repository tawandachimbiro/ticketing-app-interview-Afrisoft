-- Initial database setup script
-- This script runs when the MariaDB container is first created

-- Ensure the database exists
CREATE DATABASE IF NOT EXISTS ticketing_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant privileges
GRANT ALL PRIVILEGES ON ticketing_app.* TO 'ticketing_user'@'%';
FLUSH PRIVILEGES;

USE ticketing_app;

-- You can add initial data or table structures here if needed
-- The Spring Boot application will handle table creation via Hibernate
