package com.changamire.event;

import com.changamire.enums.TicketCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Ticket Type Quantity DTO
 * 
 * This data transfer object represents the quantity of tickets to purchase
 * for a specific ticket category (e.g., VIP, STANDARD).
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Data
public class TicketTypeQuantity {
    @NotNull
    private TicketCategory category;
    
    @NotNull
    @Min(1)
    private Integer quantity;
}