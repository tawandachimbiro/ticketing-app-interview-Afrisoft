package com.changamire.payment;

import com.changamire.RandomTransactionGenerator;
import com.changamire.enums.PaymentMethod;
import com.changamire.enums.Status;
import lombok.RequiredArgsConstructor;
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

        // Fix the endpoint URL to match the API documentation
        CardPaymentResponse response = restTemplate.postForObject(
                "/transactions",  // Changed from "/card-transactions"
                new HttpEntity<>(apiRequest, createHeaders()),
                CardPaymentResponse.class
        );

        // Save transaction with hosted URL
        Transaction transaction = Transaction.builder()
                .transactionId(response.getTransactionId())
                .reference(transactionRef)
                .status(Status.PENDING)
                .amount(BigDecimal.valueOf(request.getAmount()))
                .currency(request.getCurrency())
                .email(request.getEmail())
                .paymentMethod(method)
                .hostedUrl(response.getHostedUrl())  // Make sure Transaction entity has this field
                .checkoutId(response.getCheckoutId())
                .build();

        transactionRepository.save(transaction);

        return response;
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
//        CardPaymentResponse response = restTemplate.postForObject(
//                "/card-transactions",
//                new HttpEntity<>(apiRequest, createHeaders()),
//                CardPaymentResponse.class
//        );
//
//        Transaction transaction = Transaction.builder()
//                .transactionId(response.getTransactionId())
//                .reference(transactionRef)
//                .status(Status.PENDING)
//                .amount(BigDecimal.valueOf(request.getAmount()))
//                .currency(request.getCurrency())
//                .email(request.getEmail())
//                .paymentMethod(method)
//                .hostedUrl(response.getHostedUrl())
//                .checkoutId(response.getCheckoutId())
//                .build();
//
//        transactionRepository.save(transaction);
//
//        return response;
//    }
//
//    private HttpHeaders createHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        return headers;
//    }
//}