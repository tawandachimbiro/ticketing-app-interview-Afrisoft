package com.changamire.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Event Filter Request DTO
 * <p>
 * Encapsulates all filter parameters for event search and filtering.
 * Used to clean up the controller method signature by replacing multiple
 * @RequestParam annotations with a single request object.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterRequest {
    
    private String name;
    private String city;
    private String type;
    private String ispromotion;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;
    
    private Double minPrice;
    private Double maxPrice;
    
    @Builder.Default
    private int page = 0;
    
    @Builder.Default
    private int size = 10;
}
