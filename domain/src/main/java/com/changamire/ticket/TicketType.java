package com.changamire.ticket;

import com.changamire.enums.TicketCategory;
import com.changamire.event.Event;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TicketType {

    @Id
    @GenericGenerator(name = "alphanumeric_id", strategy = "com.changamire.AlphaNumericIdGenerator") // Custom generator
    @GeneratedValue(generator = "alphanumeric_id")
    @Column(length = 12) // Ensure the column supports 12 characters
    private String id;

    @Enumerated(EnumType.STRING)
    private TicketCategory category;

    private Double price;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(unique = true)
    private String qrCodePath;

    private boolean redeemed = false;



}
