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

//    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<TicketType> ticketTypes = new ArrayList<>();



//    endpoints pagiantion
//            list all events
//            filter by date /name /city/price /type /promotion
//            load events in the db
//    -----------------------------------------------------------------
//    cart
//    ----------------------------------------------------------------
//    send ticket event after purchase
//    sned email with tickit details
//    ----------------------------------
//    logic to pay for and event
//


//    public void addTicketType(TicketType ticketType) {
//        ticketTypes.add(ticketType);
//        ticketType.setEvent(this);
//    }
//
//    public void removeTicketType(TicketType ticketType) {
//        ticketTypes.remove(ticketType);
//        ticketType.setEvent(null);
//    }
}
