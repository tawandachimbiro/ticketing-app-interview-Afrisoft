package com.changamire.event;

import com.changamire.ticket.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Ticket Type Repository
 * 
 * This repository manages ticket type entities including purchased tickets,
 * QR codes, and redemption status. Uses alphanumeric String IDs.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public interface  TicketTypeRepository extends JpaRepository<TicketType, String> {

    /**
     * Find tickets purchased by a given customer email, newest first.
     */
    List<TicketType> findByCustomerEmailOrderByCreatedDateDesc(String customerEmail);
}
