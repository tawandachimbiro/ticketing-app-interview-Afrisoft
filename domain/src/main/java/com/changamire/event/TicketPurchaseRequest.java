package com.changamire.event;

import com.changamire.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * Ticket Purchase Request DTO
 * 
 * This data transfer object captures ticket purchase information including
 * event ID, ticket quantities by type, payment method, and customer details.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Data
public class TicketPurchaseRequest {
    @NotNull
    private Long eventId;

    @Valid
    @NotEmpty
    private List<TicketTypeQuantity> tickets; // Changed from single quantity
    
    @NotNull
    private PaymentMethod paymentMethod;
    
    @NotBlank
    @Email
    private String customerEmail;
    
    @NotBlank
    private String customerName;
    
    @Pattern(regexp = "^07\\d{8}$")
    private String mobileNumber;
}