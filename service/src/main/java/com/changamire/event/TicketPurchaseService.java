package com.changamire.event;

import com.changamire.PaymentRequest;
import com.changamire.PaymentResponse;
import com.changamire.enums.Currency;
import com.changamire.enums.Status;
import com.changamire.payment.PaymentService;
import com.changamire.payment.TransactionRepository;
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
    private final EmailService emailService;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TicketPurchaseResponse purchaseTicket(TicketPurchaseRequest request) {
        // Validate event capacity
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        
        if (event.getCapacity() <= 0) {
            return new TicketPurchaseResponse(false, "Event is sold out", null, null);
        }

        // Create payment request
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(calculateTotalAmount(event, request.getQuantity()));
        paymentRequest.setEmail(request.getCustomerEmail());
        paymentRequest.setMobileMoneyNumber(request.getMobileNumber());
        paymentRequest.setPaymentMethod(request.getPaymentMethod());
        paymentRequest.setCurrency(Currency.USD); // Adjust based on your needs

        // Process payment
        PaymentResponse paymentResponse = paymentService.processPayment(paymentRequest);

        if (paymentResponse.getStatus() == Status.SUCCESS) {
            // Update event capacity
            event.setCapacity(event.getCapacity() - request.getQuantity());
            eventRepository.save(event);

            // Generate ticket
            String ticketDetails = generateTicketDetails(event, request);

            // Send confirmation email
            emailService.sendTicketConfirmation(
                    request.getCustomerEmail(),
                    "Your Ticket Confirmation",
                    ticketDetails
            );

            return new TicketPurchaseResponse(
                    true,
                    "Purchase successful",
                    paymentResponse.getTransactionId(),
                    ticketDetails
            );
        }
        
        return new TicketPurchaseResponse(false, "Payment failed", null, null);
    }

    private Double calculateTotalAmount(Event event, Integer quantity) {
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