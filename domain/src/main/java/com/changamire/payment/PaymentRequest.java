package com.changamire.payment;

import com.changamire.enums.Currency;
import com.changamire.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Payment Request DTO
 * <p>
 * This data transfer object encapsulates mobile money payment request information
 * including amount, customer details, mobile number, and callback URLs.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public record PaymentRequest(
    Double amount,
    String email,
    
    @JsonProperty("mobile_money_number")
    @Schema(example = "07XXXXXXXX", description = "10-digit number starting with 07")
    String mobileMoneyNumber,
    
    Currency currency,
    
    @JsonProperty("payment_method_code")
    PaymentMethod paymentMethod,
    
    @JsonProperty("success_url")
    @Schema(example = "https://www.google.com/success", description = "URL to redirect on success")
    String successUrl,
    
    @JsonProperty("failure_url")
    @Schema(example = "https://www.google.com/failure", description = "URL to redirect on failure")
    String failureUrl
) {}



