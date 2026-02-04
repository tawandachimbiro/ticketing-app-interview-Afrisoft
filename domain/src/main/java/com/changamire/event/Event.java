package com.changamire.event;

import com.changamire.ticket.TicketType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Event Entity
 * <p>
 * This entity represents an event in the ticketing system including venue details,
 * location information, capacity, and associated ticket types. Events can be
 * concerts, sports events, conferences, or any ticketed gatherings.
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
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDateTime dateTime;
    private String venue;
    private String address;
    private String city;
    private String type;
    private String ispromotion;
    private String latitude;
    private String longitude;
    private Integer capacity;
    private String description;
    private String banner_url;
    private String image_url;

    @JsonManagedReference
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketType> ticketTypes = new ArrayList<>();

           //TO DO AND QR CODE FILED
}
