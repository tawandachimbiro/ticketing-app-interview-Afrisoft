package com.changamire.ticket;

/**
 * Ticket Purchase DTO
 * <p>
 * This data transfer object captures bus ticket purchase information
 * including ticket ID, quantity, customer email, and payment method.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public record TicketPurchaseDTO(
    String customerEmail,
    String ticketId,
    int quantity,
    String paymentMethod
) {}