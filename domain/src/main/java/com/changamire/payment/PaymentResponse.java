package com.changamire.payment;

import com.changamire.enums.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Payment Response DTO
 * 
 * This data transfer object contains the response from the payment gateway
 * for mobile money transactions including transaction ID, status, and messages.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Data
@Getter
@Setter
public class PaymentResponse {
    private String result;
    @Enumerated(EnumType.STRING)
    private Status status;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("transaction_reference")
    private String transactionReference;
    private String message;
    @JsonProperty("text_message")
    private String textMessage;
    @JsonProperty("qr_code")
    private String qrCode;
    private String code;
}
