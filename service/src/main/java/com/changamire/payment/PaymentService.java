package com.changamire.payment;


import com.changamire.enums.Currency;
import com.changamire.enums.PaymentMethod;
import com.changamire.enums.Status;
import com.changamire.exceptions.RecordNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final RestTemplate restTemplate;
    private final TransactionRepository transactionRepository;

    public PaymentResponse processPayment(PaymentRequest request) {
        // Generate transaction reference
        String transactionRef = "MOTAPAPAY-" + RandomTransactionGenerator.generateRandomNumbers();

        // Build API request body
        Map<String, Object> apiRequest = new HashMap<>();
        apiRequest.put("amount", request.getAmount());
        apiRequest.put("email", request.getEmail());
        apiRequest.put("mobile_money_number", request.getMobileMoneyNumber());
        apiRequest.put("currency", request.getCurrency().name());
        apiRequest.put("transaction_reference", transactionRef);
        apiRequest.put("payment_method_type", request.getPaymentMethod().getType());
        apiRequest.put("payment_method_code", request.getPaymentMethod().getCode());
        apiRequest.put("requested_response", "success"); // Default to success
        apiRequest.put("success_url", request.getSuccessUrl());
        apiRequest.put("failure_url", request.getFailureUrl());

        // Call external API
        PaymentResponse response = restTemplate.postForObject(
                "/transactions",
                new HttpEntity<>(apiRequest, createHeaders()),
                PaymentResponse.class
        );

        // Save transaction
        Transaction transaction = Transaction.builder()
                .transactionId(response.getTransactionId())
                .reference(transactionRef)
                .status(Status.valueOf(String.valueOf(response.getStatus())))
                .amount(BigDecimal.valueOf(request.getAmount()))
                .currency(Currency.valueOf(request.getCurrency().name()))
                .email(request.getEmail())
                .paymentMethod(PaymentMethod.valueOf(request.getPaymentMethod().name().toString()))
                .successUrl(request.getSuccessUrl())
                .failureUrl(request.getFailureUrl())
                .build();

        transactionRepository.save(transaction);

        return response;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public Transaction getTransactionByReference(String reference) {
        return transactionRepository.findByReference(reference)
                .orElseThrow(() -> new RecordNotFoundException("Transaction not found with reference: " + reference));
    }

    public Page<Transaction> getTransactionsByStatus(Status status, Pageable pageable) {
        return transactionRepository.findAllByStatus(status, pageable);
    }

    public Page<Transaction> getTransactionsByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable) {
        return transactionRepository.findAllByPaymentMethod(paymentMethod, pageable);
    }

    public Page<Transaction> getTransactionsByCurrency(Currency currency, Pageable pageable) {
        return transactionRepository.findAllByCurrency(currency, pageable);
    }
}





