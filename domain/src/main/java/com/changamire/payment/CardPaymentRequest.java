package com.changamire.payment;

import com.changamire.enums.Currency;

/**
 * Card Payment Request DTO
 * <p>
 * This data transfer object encapsulates card payment request information.
 * Card details are handled externally through hosted payment pages.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public record CardPaymentRequest(
    Double amount,
    String email,
    Currency currency
) {}