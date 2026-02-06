package com.changamire.event;

import com.changamire.ticket.TicketType;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Event Update Request DTO
 * <p>
 * This data transfer object contains optional fields for partially updating
 * an existing event. All fields are nullable to support selective updates.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public record EventUpdateRequest(
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
