package com.changamire.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Bus Ticket DTO
 * 
 * This data transfer object represents bus ticket information including
 * route details, pricing, schedule times, operator, and seat availability.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusTicketDTO {
    private String ticketId;
    private String route;
    private double price;
    private String departureTime;
    private String arrivalTime;
    private String busOperator;
    private int seatAvailability;
    private String travelDate;


//    public static Object builder() {
//    }
}