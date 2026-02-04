package com.changamire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * E-Commerce Ticketing Application - Main Entry Point
 * <p>
 * This is the main Spring Boot application class for the Afrisoft ticketing system.
 * It handles event ticketing, payment processing, QR code generation, and customer management.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@SpringBootApplication
public class ECommerceAppCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceAppCustomerApplication.class, args);
    }

}
