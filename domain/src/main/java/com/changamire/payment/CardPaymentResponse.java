package com.changamire.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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