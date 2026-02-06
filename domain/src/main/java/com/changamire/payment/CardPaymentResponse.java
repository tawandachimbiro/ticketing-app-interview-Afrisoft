package com.changamire.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Card Payment Response DTO
 * <p>
 * This data transfer object contains the response from the payment gateway
 * for card transactions including hosted payment URL and checkout ID.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public record CardPaymentResponse(
    String result,
    String transactionId,
    String transactionReference,
    String message,
    Double total,
    @JsonProperty("hosted_url")
    String hostedUrl,
    @JsonProperty("checkout_id")
    String checkoutId
) {}