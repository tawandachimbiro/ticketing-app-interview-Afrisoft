package com.changamire.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Payment Processing Controller
 * <p>
 * This controller handles payment processing operations for the ticketing system.
 * It manages payment transactions, integrates with external payment gateways,
 * and provides transaction status retrieval.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing and transaction management")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Process payment", description = "Process a payment transaction for ticket purchase")
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get transaction by reference", description = "Retrieve a payment transaction by its reference number")
    @GetMapping("/{reference}")
    public ResponseEntity<Transaction> getTransactionByReference(@PathVariable String reference) {
        return ResponseEntity.ok(paymentService.getTransactionByReference(reference));
    }
}
