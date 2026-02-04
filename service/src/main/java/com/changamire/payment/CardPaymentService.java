package com.changamire.payment;

import com.changamire.RandomTransactionGenerator;
import com.changamire.enums.PaymentMethod;
import com.changamire.enums.Status;
import com.changamire.exceptions.ExternalServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Card Payment Service
 * 
 * This service handles credit and debit card payment processing through external
 * payment gateway integration. It supports both ZIMSWITCH and international card
 * payments, providing hosted payment page URLs for secure card transactions.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Service
@RequiredArgsConstructor
public class CardPaymentService {
    private final RestTemplate restTemplate;
    private final TransactionRepository transactionRepository;

    public CardPaymentResponse processCardPayment(CardPaymentRequest request, PaymentMethod method) {
        String transactionRef = "MOTAPAPAY-" + RandomTransactionGenerator.generateRandomNumbers();

        Map<String, Object> apiRequest = new HashMap<>();
        apiRequest.put("amount", request.getAmount());
        apiRequest.put("email", request.getEmail());
        apiRequest.put("currency", request.getCurrency().name());
        apiRequest.put("transaction_reference", transactionRef);
        apiRequest.put("payment_method_type", method.getType());
        apiRequest.put("payment_method_code", method.getCode());
        apiRequest.put("requested_response", "success");

        try {
            CardPaymentResponse response = restTemplate.postForObject(
                    "/transactions",
                    new HttpEntity<>(apiRequest, createHeaders()),
                    CardPaymentResponse.class
            );

            Transaction transaction = Transaction.builder()
                    .transactionId(response.getTransactionId())
                    .reference(transactionRef)
                    .status(Status.PENDING)
                    .amount(BigDecimal.valueOf(request.getAmount()))
                    .currency(request.getCurrency())
                    .email(request.getEmail())
                    .paymentMethod(method)
                    .hostedUrl(response.getHostedUrl())
                    .checkoutId(response.getCheckoutId())
                    .build();

            transactionRepository.save(transaction);

            return response;
        } catch (ResourceAccessException ex) {
            handleConnectivityError(ex);
            throw ex; // This will be caught by global handler
        }
    }

    private void handleConnectivityError(ResourceAccessException ex) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(ex);
        String errorMessage = "Payment service is currently unavailable";

        if (rootCause instanceof UnknownHostException) {
            errorMessage = "Could not connect to payment service - check internet connection";
        }

        throw new ExternalServiceUnavailableException(errorMessage);
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}

//@Service
//@RequiredArgsConstructor
//public class CardPaymentService {
//    private final RestTemplate restTemplate;
//    private final TransactionRepository transactionRepository;
//
//    public CardPaymentResponse processCardPayment(CardPaymentRequest request, PaymentMethod method) {
//        String transactionRef = "MOTAPAPAY-" + RandomTransactionGenerator.generateRandomNumbers();
//
//        Map<String, Object> apiRequest = new HashMap<>();
//        apiRequest.put("amount", request.getAmount());
//        apiRequest.put("email", request.getEmail());
//        apiRequest.put("currency", request.getCurrency().name());
//        apiRequest.put("transaction_reference", transactionRef);
//        apiRequest.put("payment_method_type", method.getType());
//        apiRequest.put("payment_method_code", method.getCode());
//        apiRequest.put("requested_response", "success");
//
//        // Fix the endpoint URL to match the API documentation
//        CardPaymentResponse response = restTemplate.postForObject(
//                "/transactions",  // Changed from "/card-transactions"
//                new HttpEntity<>(apiRequest, createHeaders()),
//                CardPaymentResponse.class
//        );
//
//        // Save transaction with hosted URL
//        Transaction transaction = Transaction.builder()
//                .transactionId(response.getTransactionId())
//                .reference(transactionRef)
//                .status(Status.PENDING)
//                .amount(BigDecimal.valueOf(request.getAmount()))
//                .currency(request.getCurrency())
//                .email(request.getEmail())
//                .paymentMethod(method)
//                .hostedUrl(response.getHostedUrl())  // Make sure Transaction entity has this field
//                .checkoutId(response.getCheckoutId())
//                .build();
//
//        transactionRepository.save(transaction);
//
//        return response;
//    }
//
//        private HttpHeaders createHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        return headers;
//    }
//}

