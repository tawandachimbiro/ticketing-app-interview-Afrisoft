package com.changamire.payment;

import com.changamire.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Card Payment Controller
 * <p>
 * This controller handles credit and debit card payment processing.
 * It integrates with external payment gateways to securely process
 * card transactions for ticket purchases.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@RestController
@RequestMapping("/api/card-payments")
@RequiredArgsConstructor
@Tag(name = "Card Payments", description = "Handle credit/debit card transactions")
public class CardPaymentController {
    private final CardPaymentService cardPaymentService;

    @Operation(summary = "Process card payment", description = "Process a credit or debit card payment transaction using specified payment method")
    @PostMapping("/{paymentMethod}")
    public ResponseEntity<CardPaymentResponse> processCardPayment(
            @PathVariable PaymentMethod paymentMethod,
            @Valid @RequestBody CardPaymentRequest request) {
        return ResponseEntity.ok(cardPaymentService.processCardPayment(request, paymentMethod));
    }
}
