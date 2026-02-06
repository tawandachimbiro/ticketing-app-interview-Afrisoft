package com.changamire.event;

import com.changamire.ticket.TicketType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Event Response DTO
 * <p>
 * This data transfer object contains complete event information returned
 * from create and update operations, including a success/status message.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Builder
public record EventResponse(
    Long id,
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
    List<TicketType> ticketTypes,
    String message
) {}
