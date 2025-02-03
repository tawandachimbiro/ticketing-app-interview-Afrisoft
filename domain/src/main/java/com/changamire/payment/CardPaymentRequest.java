package com.changamire.payment;

import com.changamire.enums.Currency;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

// CardPaymentRequest.java
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