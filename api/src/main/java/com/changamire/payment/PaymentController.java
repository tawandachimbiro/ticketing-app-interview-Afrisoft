package com.changamire.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// PaymentController.java
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "payment", description = "payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @Operation(description = "payment")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{reference}")
    public ResponseEntity<Transaction> getTransactionByReference(@PathVariable String reference) {
        return ResponseEntity.ok(paymentService.getTransactionByReference(reference));
    }



}
