package com.changamire.event;

import com.changamire.enums.TicketCategory;

/**
 * Ticket Type Quantity DTO
 * <p>
 * This data transfer object represents the quantity of tickets to purchase
 * for a specific ticket category (e.g., VIP, STANDARD).
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public record TicketTypeQuantity(
    TicketCategory category,
    Integer quantity
) {}