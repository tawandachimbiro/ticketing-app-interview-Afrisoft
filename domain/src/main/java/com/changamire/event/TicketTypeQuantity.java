package com.changamire.event;

import com.changamire.enums.TicketCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketTypeQuantity {
    @NotNull
    private TicketCategory category;
    
    @NotNull
    @Min(1)
    private Integer quantity;
}