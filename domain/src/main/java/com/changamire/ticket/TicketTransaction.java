package com.changamire.ticket;

import com.changamire.base.AbstractAuditingEntity;
import com.changamire.bus.BusSchedule;
import com.changamire.customer.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Ticket Transaction Entity
 * <p>
 * This entity represents a bus ticket purchase transaction including
 * purchase date/time, total amount, customer, bus schedule, and payment details.
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
public class TicketTransaction extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate purchaseDate;
    private LocalTime purchaseTime;
    private BigDecimal totalAmount;
    @ManyToOne
    @JoinColumn(name = "bus_schedule_id")
    private BusSchedule busSchedule;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    private String paymentMethod;
    private String paymentStatus;
}
