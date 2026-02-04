package com.changamire.event;

import com.changamire.ticket.TicketType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {
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
    private List<TicketType> ticketTypes;
    private String message;
}
