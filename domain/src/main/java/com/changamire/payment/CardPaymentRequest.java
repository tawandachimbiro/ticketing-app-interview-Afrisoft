package com.changamire.payment;

import com.changamire.enums.Currency;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Card Payment Request DTO
 * 
 * This data transfer object encapsulates card payment request information.
 * Card details are handled externally through hosted payment pages.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Getter
@Setter
public class CardPaymentRequest {
    @NotNull @Positive
    private Double amount;

    @NotBlank @Email
    private String email;

    @NotNull
    private Currency currency;

    // No card details here since they're handled externally
}