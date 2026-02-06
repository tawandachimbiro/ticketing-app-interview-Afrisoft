package com.changamire.bus;

import com.changamire.base.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Bus Schedule Entity
 * <p>
 * This entity represents a specific bus schedule for a route including
 * travel date, departure/arrival times, and seat availability tracking.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BusSchedule extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ticketId;
    private LocalDate travelDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private int availableSeats;


    @ManyToOne
    @JoinColumn(name = "bus_route_id", nullable = false)
    private BusRoute busRoute;




}
