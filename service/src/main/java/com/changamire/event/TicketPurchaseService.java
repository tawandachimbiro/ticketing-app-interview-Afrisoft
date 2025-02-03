package com.changamire.event;

import com.changamire.enums.PaymentMethod;
import com.changamire.exceptions.PaymentProcessingException;
import com.changamire.payment.*;
import com.changamire.enums.Currency;
import com.changamire.enums.Status;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketPurchaseService {
    private final EventRepository eventRepository;
    private final PaymentService paymentService;
    private final CardPaymentService cardPaymentService;
    private final EmailService emailService;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TicketPurchaseResponse purchaseTicket(TicketPurchaseRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        if (event.getCapacity() <= 0) {
            return new TicketPurchaseResponse(false, "Event is sold out", null, null);
        }

        try {
            if (isCardPayment(request.getPaymentMethod())) {
                return processCardPayment(event, request);
            } else {
                return processMobileMoneyPayment(event, request);
            }
        } catch (PaymentProcessingException e) {
            return new TicketPurchaseResponse(false, e.getMessage(), null, null);
        }
    }

    private boolean isCardPayment(PaymentMethod method) {
        return method == PaymentMethod.ZIMSWITCH || method == PaymentMethod.INTERNATIONAL_CARD;
    }

    private TicketPurchaseResponse processCardPayment(Event event, TicketPurchaseRequest request) {
        CardPaymentRequest cardRequest = new CardPaymentRequest();
        cardRequest.setAmount(calculateTotalAmount(event, request.getQuantity()));
        cardRequest.setEmail(request.getCustomerEmail());
        cardRequest.setCurrency(Currency.USD);

        CardPaymentResponse paymentResponse = cardPaymentService.processCardPayment(cardRequest, request.getPaymentMethod());

        return handleSuccessfulPayment(event, request, paymentResponse);
    }

    private TicketPurchaseResponse processMobileMoneyPayment(Event event, TicketPurchaseRequest request) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(calculateTotalAmount(event, request.getQuantity()));
        paymentRequest.setEmail(request.getCustomerEmail());
        paymentRequest.setMobileMoneyNumber(request.getMobileNumber());
        paymentRequest.setPaymentMethod(request.getPaymentMethod());
        paymentRequest.setCurrency(Currency.USD);

        PaymentResponse paymentResponse = paymentService.processPayment(paymentRequest);

        return handleSuccessfulPayment(event, request, paymentResponse);
    }

    private TicketPurchaseResponse handleSuccessfulPayment(Event event,
                                                           TicketPurchaseRequest request,
                                                           Object paymentResponse) {
        if (paymentResponse instanceof PaymentResponse pr && pr.getStatus() == Status.SUCCESS ||
                paymentResponse instanceof CardPaymentResponse cpr && cpr.getResult().equals("success")) {

            event.setCapacity(event.getCapacity() - request.getQuantity());
            eventRepository.save(event);

            String ticketDetails = generateTicketDetails(event, request);
            emailService.sendTicketConfirmation(
                    request.getCustomerEmail(),
                    "Your Ticket Confirmation",
                    ticketDetails
            );

            return new TicketPurchaseResponse(
                    true,
                    "Purchase successful",
                    getTransactionId(paymentResponse),
                    ticketDetails
            );
        }
        return new TicketPurchaseResponse(false, "Payment failed", null, null);
    }

    private String getTransactionId(Object response) {
        if (response instanceof PaymentResponse pr) return pr.getTransactionId();
        if (response instanceof CardPaymentResponse cpr) return cpr.getTransactionId();
        return null;
    }


    private @NotNull(message = "Amount is required") @Positive(message = "Amount must be positive") Double calculateTotalAmount(Event event, Integer quantity) {
        return event.getTicketTypes().stream()
                .findFirst()
                .map(t -> t.getPrice() * quantity)
                .orElseThrow(() -> new IllegalStateException("Event has no ticket types"));
    }

    private String generateTicketDetails(Event event, TicketPurchaseRequest request) {
        return String.format(
                "Event: %s\nDate: %s\nVenue: %s\nQuantity: %d\nTotal: $%.2f",
                event.getName(),
                event.getDateTime(),
                event.getVenue(),
                request.getQuantity(),
                calculateTotalAmount(event, request.getQuantity()) // Now returns Double
        );
    }
}




//@Service
//@RequiredArgsConstructor
//public class TicketPurchaseService {
//    private final EventRepository eventRepository;
//    private final PaymentService paymentService;
//    private final EmailService emailService;
//    private final TransactionRepository transactionRepository;
//
//    @Transactional
//    public TicketPurchaseResponse purchaseTicket(TicketPurchaseRequest request) {
//        // Validate event capacity
//        Event event = eventRepository.findById(request.getEventId())
//                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
//
//        if (event.getCapacity() <= 0) {
//            return new TicketPurchaseResponse(false, "Event is sold out", null, null);
//        }
//
//        // Create payment request
//        PaymentRequest paymentRequest = new PaymentRequest();
//        paymentRequest.setAmount(calculateTotalAmount(event, request.getQuantity()));
//        paymentRequest.setEmail(request.getCustomerEmail());
//        paymentRequest.setMobileMoneyNumber(request.getMobileNumber());
//        paymentRequest.setPaymentMethod(request.getPaymentMethod());
//        paymentRequest.setCurrency(Currency.USD); // Adjust based on your needs
//
//        // Process payment
//        PaymentResponse paymentResponse = paymentService.processPayment(paymentRequest);
//
//        if (paymentResponse.getStatus() == Status.SUCCESS) {
//            // Update event capacity
//            event.setCapacity(event.getCapacity() - request.getQuantity());
//            eventRepository.save(event);
//
//            // Generate ticket
//            String ticketDetails = generateTicketDetails(event, request);
//
//            // Send confirmation email
//            emailService.sendTicketConfirmation(
//                    request.getCustomerEmail(),
//                    "Your Ticket Confirmation",
//                    ticketDetails
//            );
//
//            return new TicketPurchaseResponse(
//                    true,
//                    "Purchase successful",
//                    paymentResponse.getTransactionId(),
//                    ticketDetails
//            );
//        }
//        //do proper error hanlding
//        return new TicketPurchaseResponse(false, "Payment failed", null, null);
//    }
//
//    private Double calculateTotalAmount(Event event, Integer quantity) {
//        return event.getTicketTypes().stream()
//                .findFirst()
//                .map(t -> t.getPrice() * quantity)
//                .orElseThrow(() -> new IllegalStateException("Event has no ticket types"));
//    }
//
//    private String generateTicketDetails(Event event, TicketPurchaseRequest request) {
//        return String.format(
//                "Event: %s\nDate: %s\nVenue: %s\nQuantity: %d\nTotal: $%.2f",
//                event.getName(),
//                event.getDateTime(),
//                event.getVenue(),
//                request.getQuantity(),
//                calculateTotalAmount(event, request.getQuantity()) // Now returns Double
//        );
//    }
//}