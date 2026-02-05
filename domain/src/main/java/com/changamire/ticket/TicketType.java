package com.changamire.ticket;

import com.changamire.base.AbstractAuditingEntity;
import com.changamire.enums.TicketCategory;
import com.changamire.event.Event;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

/**
 * Ticket Type Entity
 * <p>
 * This entity represents an individual purchased ticket with a unique
 * alphanumeric ID, category (VIP, STANDARD, etc.), price, QR code path,
 * and redemption status for event entry validation.
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
public class TicketType extends AbstractAuditingEntity {

    @Id
    @GenericGenerator(name = "alphanumeric_id", strategy = "com.changamire.AlphaNumericIdGenerator") // Custom generator
    @GeneratedValue(generator = "alphanumeric_id")
    @Column(length = 12) // Ensure the column supports 12 characters
    private String id;

    @Enumerated(EnumType.STRING)
    private TicketCategory category;

    private Double price;

    @Column(name = "customer_email", length = 150)
    private String customerEmail;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(unique = true)
    private String qrCodePath;

    private boolean redeemed = false;



}
