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
import java.util.UUID;

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
    
    // TEST MODE: Set to true for development/testing, false for production
    // TODO: Move to configuration when ready for production
    private static final boolean TEST_MODE = true;

    public CardPaymentResponse processCardPayment(CardPaymentRequest request, PaymentMethod method) {
        var transactionRef = "MOTAPAPAY-" + RandomTransactionGenerator.generateRandomNumbers();
        
        // TEST MODE: Return success without calling external API
        if (TEST_MODE) {
            return createTestModeSuccessResponse(transactionRef, request, method);
        }

        var apiRequest = new HashMap<String, Object>();
        apiRequest.put("amount", request.amount());
        apiRequest.put("email", request.email());
        apiRequest.put("currency", request.currency().name());
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

            var transaction = Transaction.builder()
                    .transactionId(response.transactionId())
                    .reference(transactionRef)
                    .status(Status.PENDING)
                    .amount(BigDecimal.valueOf(request.amount()))
                    .currency(request.currency())
                    .email(request.email())
                    .paymentMethod(method)
                    .hostedUrl(response.hostedUrl())
                    .checkoutId(response.checkoutId())
                    .build();

            transactionRepository.save(transaction);

            return response;
        } catch (ResourceAccessException ex) {
            handleConnectivityError(ex);
            throw ex; // This will be caught by global handler
        }
    }

    private void handleConnectivityError(ResourceAccessException ex) {
        var rootCause = NestedExceptionUtils.getRootCause(ex);
        var errorMessage = "Payment service is currently unavailable";

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
    
    /**
     * Create a test mode success response for card payments without calling external API
     * Used for development and testing when payment.test-mode=true
     */
    private CardPaymentResponse createTestModeSuccessResponse(String transactionRef, CardPaymentRequest request, PaymentMethod method) {
        var testTransactionId = "TEST-CARD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        var testCheckoutId = "CHECKOUT-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        
        // Save test transaction to database
        var transaction = Transaction.builder()
                .transactionId(testTransactionId)
                .reference(transactionRef)
                .status(Status.SUCCESS)
                .amount(BigDecimal.valueOf(request.amount()))
                .currency(request.currency())
                .email(request.email())
                .paymentMethod(method)
                .hostedUrl("https://test-payment-page.local/checkout/" + testCheckoutId)
                .checkoutId(testCheckoutId)
                .build();
        
        transactionRepository.save(transaction);
        
        // Return success response
        return new CardPaymentResponse(
                "success",                                              // result
                testTransactionId,                                      // transactionId
                transactionRef,                                         // transactionReference
                "Test mode: Card payment processed successfully",       // message
                request.amount(),                                       // total (Double)
                "https://test-payment-page.local/checkout/" + testCheckoutId,  // hostedUrl
                testCheckoutId                                          // checkoutId
        );
    }
}


