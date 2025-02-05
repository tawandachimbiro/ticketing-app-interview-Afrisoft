package com.changamire.event;

import com.changamire.enums.PaymentMethod;
import com.changamire.exceptions.EventNotFoundException;
import com.changamire.exceptions.PaymentProcessingException;
import com.changamire.exceptions.TicketsSoldOutException;
import com.changamire.payment.*;
import com.changamire.enums.Currency;
import com.changamire.enums.Status;
import com.changamire.ticket.TicketType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketPurchaseService {
    private final EventRepository eventRepository;
    private final PaymentService paymentService;
    private final CardPaymentService cardPaymentService;
    private final EmailService emailService;


    private final TicketTypeRepository ticketTypeRepository;

    @Transactional
    public TicketPurchaseResponse purchaseTicket(TicketPurchaseRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException ("Event not found"));

        // Validate capacity before processing payment
        if (event.getCapacity() < request.getQuantity()) {
            throw new TicketsSoldOutException("Tickets are sold out");
        }


        try {
            Object paymentResponse = processPayment(event, request);

            if (isPaymentSuccessful(paymentResponse)) {
                List<TicketType> purchasedTickets = generateAndPersistTickets(event, request);
                updateEventCapacity(event, request);
                String ticketDetails = generateTicketDetails(event, request, purchasedTickets, paymentResponse);

                emailService.sendTicketConfirmation(
                        request.getCustomerEmail(),
                        "Your Ticket Confirmation - " + event.getName(),
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

        } catch (PaymentProcessingException e) {
            return new TicketPurchaseResponse(false, e.getMessage(), null, null);
        }
    }

    private Object processPayment(Event event, TicketPurchaseRequest request) {
        if (isCardPayment(request.getPaymentMethod())) {
            return processCardPayment(event, request);
        } else {
            return processMobileMoneyPayment(event, request);
        }
    }

    private boolean isCardPayment(PaymentMethod method) {
        return method == PaymentMethod.ZIMSWITCH || method == PaymentMethod.INTERNATIONAL_CARD;
    }

    private CardPaymentResponse processCardPayment(Event event, TicketPurchaseRequest request) {
        CardPaymentRequest cardRequest = new CardPaymentRequest();
        cardRequest.setAmount(calculateTotalAmount(event, request.getQuantity()));
        cardRequest.setEmail(request.getCustomerEmail());
        cardRequest.setCurrency(Currency.USD);

        return cardPaymentService.processCardPayment(cardRequest, request.getPaymentMethod());
    }

    private PaymentResponse processMobileMoneyPayment(Event event, TicketPurchaseRequest request) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(calculateTotalAmount(event, request.getQuantity()));
        paymentRequest.setEmail(request.getCustomerEmail());
        paymentRequest.setMobileMoneyNumber(request.getMobileNumber());
        paymentRequest.setPaymentMethod(request.getPaymentMethod());
        paymentRequest.setCurrency(Currency.USD);

        return paymentService.processPayment(paymentRequest);
    }

    private List<TicketType> generateAndPersistTickets(Event event, TicketPurchaseRequest request) {
        List<TicketType> tickets = new ArrayList<>();
        TicketType baseType = event.getTicketTypes().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Event has no ticket types"));

        for (int i = 0; i < request.getQuantity(); i++) {
            TicketType ticket = new TicketType();
            ticket.setCategory(baseType.getCategory());
            ticket.setPrice(baseType.getPrice());
            ticket.setEvent(event);
            ticketTypeRepository.save(ticket);  // Persist each ticket
            tickets.add(ticket);
        }
        return tickets;
    }

    private void updateEventCapacity(Event event, TicketPurchaseRequest request) {
        event.setCapacity(event.getCapacity() - request.getQuantity());
        eventRepository.save(event);
    }

    private boolean isPaymentSuccessful(Object paymentResponse) {
        return (paymentResponse instanceof PaymentResponse pr && pr.getStatus() == Status.SUCCESS) ||
                (paymentResponse instanceof CardPaymentResponse cpr && cpr.getResult().equalsIgnoreCase("success"));
    }

    private String getTransactionId(Object response) {
        if (response instanceof PaymentResponse pr) return pr.getTransactionId();
        if (response instanceof CardPaymentResponse cpr) return cpr.getTransactionId();
        return null;
    }

    private Double calculateTotalAmount(Event event, Integer quantity) {
        return event.getTicketTypes().stream()
                .findFirst()
                .map(t -> t.getPrice() * quantity)
                .orElseThrow(() -> new IllegalStateException("Event has no ticket types"));
    }

    private String generateTicketDetails(Event event,
                                         TicketPurchaseRequest request,
                                         List<TicketType> tickets,
                                         Object paymentResponse) {
        String ticketListHtml = tickets.stream()
                .map(t -> String.format(
                        "<li style='margin-bottom: 10px;'>" +
                                "🎫 Ticket ID: <strong>%s</strong><br>" +
                                "📌 Type: %s" +
                                "</li>",
                        t.getId(),
                        t.getCategory().name()))
                .collect(Collectors.joining());

        int quantity = request.getQuantity();
        double totalAmount = calculateTotalAmount(event, quantity);

        return String.format(
                loadEmailTemplate(),
                event.getName(),                                  // 1. %s
                event.getDateTime().format(                       // 2. %s
                        DateTimeFormatter.ofPattern("EEE, MMM dd yyyy hh:mm a")),
                event.getVenue(),                                 // 3. %s
                event.getCity(),                                  // 4. %s
                quantity,                                         // 5. %d
                totalAmount,                                      // 6. %.2f
                ticketListHtml                                    // 7. %s
        );
    }

    private String loadEmailTemplate() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("app/src/main/resources/templates/email/ticket-confirmation.html")) {
            if (is != null) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } else {
                throw new IOException("Template not found in resources");
            }
        } catch (IOException e) {
            // Fallback template matching HTML structure
            return """
                    <!DOCTYPE html>
                    <html>
                    <body>
                        <h2>🎟️ Your Ticket Confirmation.</h2>
                        <p>Thank you for purchasing tickets for <strong>%s</strong>!</p>
                        
                        <div style="border: 1px solid #e0e0e0; padding: 20px; margin: 20px 0;">
                            <h3>Event Details</h3>
                            <p>📅 Date & Time: %s</p>
                            <p>📍 Venue: %s</p>
                            <p>🏙️ City: %s</p>
                            <p>🔢 Total Tickets: %d</p>
                            <p>💵 Total Paid: $%.2f</p>
                            
                            <h3>Your Tickets</h3>
                            <ul style="list-style: none; padding: 0;">
                                %s
                            </ul>
                        </div>
                    </body>
                    </html>""";
        }
    }
}