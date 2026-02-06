package com.changamire.ticket;


import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Ticket Transaction Repository
 * <p>
 * This repository manages bus ticket purchase transactions including
 * customer information, payment details, and bus schedule associations.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public interface  TicketTransactionRepository extends JpaRepository<TicketTransaction, Long> {
}
