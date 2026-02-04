package com.changamire.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Card Payment Response DTO
 * 
 * This data transfer object contains the response from the payment gateway
 * for card transactions including hosted payment URL and checkout ID.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Data
public class CardPaymentResponse {
    private String result;
    private String transactionId;
    private String transactionReference;
    private String message;
    private Double total;

    @JsonProperty("hosted_url")
    private String hostedUrl;

    @JsonProperty("checkout_id")
    private String checkoutId;
}