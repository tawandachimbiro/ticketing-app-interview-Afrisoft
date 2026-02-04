package com.changamire.customer;

import com.changamire.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Customer Repository
 * 
 * This repository manages customer data including lookups by email address
 * for ticket purchase and customer management operations.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public interface CustomerRepository  extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email );
}
