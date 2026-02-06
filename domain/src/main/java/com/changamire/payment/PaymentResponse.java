package com.changamire.payment;

import com.changamire.enums.Status;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payment Response DTO
 * <p>
 * This data transfer object contains the response from the payment gateway
 * for mobile money transactions including transaction ID, status, and messages.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public record PaymentResponse(
    String result,
    Status status,
    @JsonProperty("transaction_id")
    String transactionId,
    @JsonProperty("transaction_reference")
    String transactionReference,
    String message,
    @JsonProperty("text_message")
    String textMessage,
    @JsonProperty("qr_code")
    String qrCode,
    String code
) {}
