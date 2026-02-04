package com.changamire.payment;

import com.changamire.enums.Currency;
import com.changamire.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

/**
 * Payment Request DTO
 * 
 * This data transfer object encapsulates mobile money payment request information
 * including amount, customer details, mobile number, and callback URLs.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Getter
@Setter
@Data
public class PaymentRequest {
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double  amount;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Mobile money number is required")
    @Pattern(regexp = "^07\\d{8}$", message = "Invalid mobile money number")
    @JsonProperty("mobile_money_number")
    @Schema(example = "07XXXXXXXX", description = "10-digit number starting with 07")
    private String mobileMoneyNumber;

    @NotNull(message = "Currency is required")
    private Currency currency;

    @NotNull(message = "Payment method is required")
    @JsonProperty("payment_method_code")
    private PaymentMethod paymentMethod;

    @NotBlank(message = "Success URL is required")
    @JsonProperty("success_url")
    @Schema(example = "https://www.google.com/success", description = "URL to redirect on success")
    @Pattern(regexp = "^https?://.*", message = "Invalid URL format")
    private String successUrl;

    @NotBlank(message = "Failure URL is required")
    @JsonProperty("failure_url")
    @Schema(example = "https://www.google.com/failure", description = "URL to redirect on failure")
    @Pattern(regexp = "^https?://.*", message = "Invalid URL format")
    private String failureUrl;


}












//@Data
//@Getter
//@Setter
//public class PaymentRequest {
//    private Integer amount;
//    private String email;
//    @JsonProperty("mobile_money_number")
//    private String mobileMoneyNumber;
//
//    @Enumerated(EnumType.STRING)
//    private Currency currency;
//
//    @JsonProperty("transaction_reference")
//    private String transactionReference;
//
//    @JsonProperty("payment_method_type")
//    private String paymentMethodType;
//    @JsonProperty("payment_method_code")
//    private String paymentMethodCode;
//    @JsonProperty("requested_response")
//    private String requestedResponse;
//
//    // Getters and Setters
//}
