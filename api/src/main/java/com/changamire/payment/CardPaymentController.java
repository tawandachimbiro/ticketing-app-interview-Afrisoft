package com.changamire.payment;

import com.changamire.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/card-payments")
@RequiredArgsConstructor
@Tag(name = "Card Payments", description = "Handle credit/debit card transactions")
public class CardPaymentController {
    private final CardPaymentService cardPaymentService;

    @PostMapping("/{paymentMethod}")
    @Operation(summary = "Process card payment")
    public ResponseEntity<CardPaymentResponse> processCardPayment(
            @PathVariable PaymentMethod paymentMethod,
            @Valid @RequestBody CardPaymentRequest request) {
        return ResponseEntity.ok(cardPaymentService.processCardPayment(request, paymentMethod));
    }
}
