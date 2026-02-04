package com.changamire.ticket;

import lombok.Builder;

/**
 * Bus Ticket DTO
 * <p>
 * This data transfer object represents bus ticket information including
 * route details, pricing, schedule times, operator, and seat availability.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Builder
public record BusTicketDTO(
    String ticketId,
    String route,
    double price,
    String departureTime,
    String arrivalTime,
    String busOperator,
    int seatAvailability,
    String travelDate
) {}