package com.changamire.ticket;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TicketTransaction {
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
