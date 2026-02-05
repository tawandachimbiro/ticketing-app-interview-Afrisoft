package com.changamire.event;

import com.changamire.base.AbstractAuditingEntity;
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
public class Event extends AbstractAuditingEntity {
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

    @Column(columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String banner_url;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String image_url;

     // Soft delete flag and timestamp.
    @Column(nullable = false)
    private Boolean deleted = false;

    private LocalDateTime deletedAt;

    @JsonManagedReference
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TicketType> ticketTypes = new ArrayList<>();

}
