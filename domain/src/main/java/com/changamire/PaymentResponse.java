package com.changamire;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
