package com.changamire.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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