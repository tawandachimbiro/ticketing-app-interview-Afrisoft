package com.changamire.event;

import com.changamire.enums.PaymentMethod;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TicketPurchaseRequest {
    @NotNull
    private Long eventId;
    
    @NotNull
    private Integer quantity;
    
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