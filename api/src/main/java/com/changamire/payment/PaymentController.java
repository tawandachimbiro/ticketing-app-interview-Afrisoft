package com.changamire.payment;

import com.changamire.PaymentRequest;
import com.changamire.PaymentResponse;
import com.changamire.Transaction;
import com.changamire.payment.PaymentService;
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

//    @GetMapping
//    public ResponseEntity<Page<Transaction>> getTransactionsByFilter(
//            @RequestParam(required = false) Status status,
//            @RequestParam(required = false) PaymentMethod paymentMethod,
//            @RequestParam(required = false) Currency currency,
//            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
//
//        if (status != null) {
//            return ResponseEntity.ok(paymentService.getTransactionsByStatus(status, pageable));
//        }
//        if (paymentMethod != null) {
//            return ResponseEntity.ok(paymentService.getTransactionsByPaymentMethod(paymentMethod, pageable));
//        }
//        if (currency != null) {
//            return ResponseEntity.ok(paymentService.getTransactionsByCurrency(currency, pageable));
//        }
//        return ResponseEntity.badRequest().build();
//    }

}
