package com.changamire.ticket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ticket Purchase DTO
 * 
 * This data transfer object captures bus ticket purchase information
 * including ticket ID, quantity, customer email, and payment method.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketPurchaseDTO {
    private String customerEmail;
    private String ticketId; // From BusSchedule
    private int quantity;
    private String paymentMethod;
}