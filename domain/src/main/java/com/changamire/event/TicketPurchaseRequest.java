package com.changamire.event;

import com.changamire.enums.PaymentMethod;
import java.util.List;

/**
 * Ticket Purchase Request DTO
 * <p>
 * This data transfer object captures ticket purchase information including
 * event ID, ticket quantities by type, payment method, and customer details.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public record TicketPurchaseRequest(
    Long eventId,
    List<TicketTypeQuantity> tickets,
    PaymentMethod paymentMethod,
    String customerEmail,
    String customerName,
    String mobileNumber
) {}