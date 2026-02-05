package com.changamire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * E-Commerce Ticketing Application - Main Entry Point
 * <p>
 * This is the main Spring Boot application class for the Afrisoft ticketing system.
 * It handles event ticketing, payment processing, QR code generation, and customer management.
 * <p>
 * Async support is enabled to offload non-critical operations like email sending
 * to background threads, so the main HTTP request can return quickly.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@EnableAsync
@SpringBootApplication
public class ECommerceAppCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceAppCustomerApplication.class, args);
    }

    /**
     * Executor for asynchronous email tasks.
     */
    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("email-async-");
        executor.initialize();
        return executor;
    }
}
