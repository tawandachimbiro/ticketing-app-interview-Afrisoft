package com.changamire.event;

import com.changamire.ticket.TicketType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Event Create Request DTO
 * 
 * This data transfer object encapsulates all required information for creating
 * a new event including venue details, location, capacity, and ticket types.
 * Includes validation constraints for data integrity.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {
    
    @NotBlank(message = "Event name is required")
    @Size(max = 255, message = "Event name must not exceed 255 characters")
    private String name;
    
    @NotNull(message = "Event date and time is required")
    @Future(message = "Event date must be in the future")
    private LocalDateTime dateTime;
    
    @NotBlank(message = "Venue is required")
    @Size(max = 255, message = "Venue must not exceed 255 characters")
    private String venue;
    
    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @NotBlank(message = "Event type is required")
    @Size(max = 100, message = "Event type must not exceed 100 characters")
    private String type;
    
    @Size(max = 10, message = "Promotion flag must not exceed 10 characters")
    private String ispromotion;
    
    @Pattern(regexp = "^-?([1-8]?[0-9]\\.{1}\\d+|90\\.{1}0+)$", message = "Invalid latitude format")
    private String latitude;
    
    @Pattern(regexp = "^-?((1[0-7][0-9])|([1-9]?[0-9]))\\.{1}\\d+|180\\.{1}0+$", message = "Invalid longitude format")
    private String longitude;
    
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be a positive number")
    private Integer capacity;
    
    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    @Size(max = 500, message = "Banner URL must not exceed 500 characters")
    private String banner_url;
    
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String image_url;
    
    @Valid
    private List<TicketType> ticketTypes;
}
