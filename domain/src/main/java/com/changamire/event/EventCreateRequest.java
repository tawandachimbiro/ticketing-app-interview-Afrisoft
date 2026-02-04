package com.changamire.event;

import com.changamire.ticket.TicketType;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event Create Request DTO
 * <p>
 * This data transfer object encapsulates all required information for creating
 * a new event including venue details, location, capacity, and ticket types.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public record EventCreateRequest(
    String name,
    LocalDateTime dateTime,
    String venue,
    String address,
    String city,
    String type,
    String ispromotion,
    String latitude,
    String longitude,
    Integer capacity,
    String description,
    String banner_url,
    String image_url,
    List<TicketType> ticketTypes
) {}
