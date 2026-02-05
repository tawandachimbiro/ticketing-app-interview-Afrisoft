package com.changamire.event;

import com.changamire.enums.TicketCategory;

import java.time.LocalDateTime;

/**
 * DTO for exposing a user's purchased tickets on the "My Tickets" page.
 *
 * Placed in the service layer so it can be reused by different API/controllers
 * without coupling it to a specific API module.
 */
public record MyTicketResponse(
        String ticketId,
        TicketCategory category,
        Double price,
        boolean redeemed,
        String qrCodePath,
        Long eventId,
        String eventName,
        LocalDateTime eventDateTime,
        String venue,
        String city,
        LocalDateTime purchaseDate
) {
}

