package com.changamire.ticket;

import com.changamire.enums.TicketCategory;
import com.changamire.event.Event;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class TicketType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TicketCategory category;

    private Double price;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;



}
