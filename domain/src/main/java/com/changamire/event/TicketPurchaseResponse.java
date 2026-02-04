package com.changamire.event;

import com.changamire.enums.PaymentMethod;

/**
 * Ticket Purchase Response DTO
 * <p>
 * This data transfer object contains the result of a ticket purchase operation
 * including success status, transaction details, ticket information, and
 * payment gateway URLs for card payments.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public record TicketPurchaseResponse(
    boolean success,
    String message,
    String transactionId,
    String ticketDetails,
    String code,
    PaymentMethod paymentMethod,
    String hostedUrl,
    String checkoutId
) {}

