package com.changamire.event;

import com.changamire.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Ticket Purchase Response DTO
 * 
 * This data transfer object contains the result of a ticket purchase operation
 * including success status, transaction details, ticket information, and
 * payment gateway URLs for card payments.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Data
@AllArgsConstructor
public class TicketPurchaseResponse {
    private boolean success;
    private String message;
    private String transactionId;
    private String ticketDetails;
    private String code;
    private PaymentMethod paymentMethod;
    private String hostedUrl;
    private String checkoutId;
}

