package com.changamire.event;

import com.changamire.enums.PaymentMethod;
import com.changamire.enums.TicketCategory;
import com.changamire.exceptions.EventNotFoundException;
import com.changamire.exceptions.ExternalServiceUnavailableException;
import com.changamire.exceptions.PaymentProcessingException;
import com.changamire.exceptions.TicketsSoldOutException;
import com.changamire.payment.*;
import com.changamire.enums.Currency;
import com.changamire.enums.Status;
import com.changamire.ticket.TicketType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import com.changamire.event.Event;

/**
 * Ticket Purchase Service
 * 
 * This service orchestrates the complete ticket purchase workflow including
 * payment processing (mobile money and card), ticket generation with QR codes,
 * email confirmation, and event capacity management.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketPurchaseService {

    private final EventRepository eventRepository;
    private final PaymentService paymentService;
    private final CardPaymentService cardPaymentService;
    private final EmailService emailService;
    private final TicketTypeRepository ticketTypeRepository;
    private final QRCodeService qrCodeService;

    @Transactional
    public TicketPurchaseResponse purchaseTicket(TicketPurchaseRequest request) {
        var event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        var totalTickets = request.tickets().stream()
                .mapToInt(TicketTypeQuantity::quantity)
                .sum();

        if (event.getCapacity() < totalTickets) {
            throw new TicketsSoldOutException("Not enough tickets available");
        }

        try {
            var totalAmount = calculateTotalAmount(event, request.tickets());
            var paymentResponse = processPayment(event, request, totalAmount);

            if (isPaymentSuccessful(paymentResponse)) {
                var purchasedTickets = generateAndPersistTickets(event, request);
                updateEventCapacity(event, totalTickets);
                var ticketDetails = generateTicketDetails(event, request, purchasedTickets, paymentResponse, totalAmount);

                log.info(" queuing async email send ");
                log.info("To: {}", request.customerEmail());
                log.info("Subject: Your Ticket Confirmation - {}", event.getName());

                emailService.sendTicketConfirmation(
                        request.customerEmail(),
                        "Your Ticket Confirmation - " + event.getName(),
                        ticketDetails
                );

                log.info("Email send requested (async) for: {}", request.customerEmail());

                return new TicketPurchaseResponse(
                        true,
                        "Purchase successful! A confirmation email will be sent shortly.",
                        getTransactionId(paymentResponse),
                        ticketDetails,
                        getCode(paymentResponse),
                        request.paymentMethod(),
                        getHostedUrl(paymentResponse),
                        getCheckoutId(paymentResponse)
                );
            }

            return new TicketPurchaseResponse(
                    false,
                    "Payment failed",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );

        } catch (PaymentProcessingException | ExternalServiceUnavailableException | IOException e) { // Added IOException
            return new TicketPurchaseResponse(
                    false,
                    e.getMessage(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
        }
    }


    private String getCode(Object response) {
        if (response instanceof PaymentResponse pr) return pr.code();
        return null;
    }

    private String getHostedUrl(Object response) {
        if (response instanceof CardPaymentResponse cpr) return cpr.hostedUrl();
        return null;
    }

    private String getCheckoutId(Object response) {
        if (response instanceof CardPaymentResponse cpr) return cpr.checkoutId();
        return null;
    }

    private Object processPayment(Event event, TicketPurchaseRequest request, double totalAmount) {
        if (isCardPayment(request.paymentMethod())) {
            return processCardPayment(event, request, totalAmount);
        } else {
            return processMobileMoneyPayment(event, request, totalAmount);
        }
    }

    private boolean isCardPayment(PaymentMethod method) {
        return method == PaymentMethod.ZIMSWITCH || method == PaymentMethod.INTERNATIONAL_CARD;
    }

    private CardPaymentResponse processCardPayment(Event event, TicketPurchaseRequest request, double totalAmount) {
        var cardRequest = new CardPaymentRequest(
                totalAmount,
                request.customerEmail(),
                Currency.USD
        );

        return cardPaymentService.processCardPayment(cardRequest, request.paymentMethod());
    }

    private PaymentResponse processMobileMoneyPayment(Event event, TicketPurchaseRequest request, double totalAmount) {
        var paymentRequest = new PaymentRequest(
                totalAmount,
                request.customerEmail(),
                request.mobileNumber(),
                Currency.USD,
                request.paymentMethod(),
                "https://default-success-url.com",  // TODO: Get from config or request
                "https://default-failure-url.com"   // TODO: Get from config or request
        );

        return paymentService.processPayment(paymentRequest);
    }


    private List<TicketType> generateAndPersistTickets(Event event, TicketPurchaseRequest request) throws IOException {
        var tickets = new ArrayList<TicketType>();

        request.tickets().forEach(ticketRequest -> {
            var ticketTemplate = event.getTicketTypes().stream()
                    .filter(tt -> tt.getCategory() == ticketRequest.category())
                    .filter(tt -> tt.getQrCodePath() == null)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Ticket type not available: " + ticketRequest.category()));

            for (int i = 0; i < ticketRequest.quantity(); i++) {
                var ticket = new TicketType();
                ticket.setCategory(ticketTemplate.getCategory());
                ticket.setPrice(ticketTemplate.getPrice());
                ticket.setCustomerEmail(request.customerEmail());
                ticket.setEvent(event);
                ticketTypeRepository.save(ticket);

                try {
                    // Generate QR code after ticket has ID
                    var qrData = String.format(
                            "https://localhost:3000/verify-ticket?ticketId=%s&eventId=%d&type=%s",
                            ticket.getId(),
                            event.getId(),
                            ticket.getCategory()
                    );
                    var qrPath = qrCodeService.generateQRCode(qrData, 200, 200);
                    ticket.setQrCodePath(qrPath);
                    ticketTypeRepository.save(ticket);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to generate QR code", e);
                }

                tickets.add(ticket);
            }
        });

        return tickets;
    }

    private void updateEventCapacity(Event event, int totalTickets) {
        event.setCapacity(event.getCapacity() - totalTickets);
        eventRepository.save(event);
    }

    private boolean isPaymentSuccessful(Object paymentResponse) {
        return (paymentResponse instanceof PaymentResponse pr && pr.status() == Status.SUCCESS) ||
                (paymentResponse instanceof CardPaymentResponse cpr && cpr.result().equalsIgnoreCase("success"));
    }

    private String getTransactionId(Object response) {
        if (response instanceof PaymentResponse pr) return pr.transactionId();
        if (response instanceof CardPaymentResponse cpr) return cpr.transactionId();
        return null;
    }

    private Double calculateTotalAmount(Event event, List<TicketTypeQuantity> tickets) {
        return tickets.stream()
                .mapToDouble(tq -> {
                    var tt = event.getTicketTypes().stream()
                            .filter(t -> t.getCategory() == tq.category())
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Ticket type not found"));
                    return tt.getPrice() * tq.quantity();
                })
                .sum();
    }


    private String generateTicketDetails(Event event,
                                         TicketPurchaseRequest request,
                                         List<TicketType> tickets,
                                         Object paymentResponse,
                                         double totalAmount) {
        var ticketsByCategory = tickets.stream()
                .collect(Collectors.groupingBy(TicketType::getCategory));

        var ticketListHtml = new StringBuilder();
        ticketsByCategory.forEach((category, ticketList) -> {
            var quantity = ticketList.size();
            var price = ticketList.get(0).getPrice();
            var subtotal = price * quantity;

            var qrCodes = ticketList.stream()
                    .map(t -> String.format(
                            "<li style='margin-bottom: 15px;'>" +
                                    "<img src='cid:%s' style='width: 150px; height: 150px;'/><br>" +
                                    "Ticket Type: %s" +
                                    "</li>",
                            t.getQrCodePath(),
                            t.getCategory()))
                    .collect(Collectors.joining());

            ticketListHtml.append(String.format(
                    "<div style='margin-bottom: 20px; border-bottom: 1px solid #eee; padding-bottom: 15px;'>" +
                            "<h4 style='color: #2c3e50; margin-top: 0;'>%s Tickets</h4>" +
                            "<p>Quantity: %d</p>" +
                            "<p>Price per ticket: $%.2f</p>" +
                            "<p>Subtotal: $%.2f</p>" +
                            "<ul style='list-style: none; padding-left: 0; margin-top: 10px;'>%s</ul>" +
                            "</div>",
                    category, quantity, price, subtotal, qrCodes
            ));
        });

        return String.format(
                loadEmailTemplate(),
                event.getName(),
                event.getDateTime().format(DateTimeFormatter.ofPattern("EEE, MMM dd yyyy hh:mm a")),
                event.getVenue(),
                event.getCity(),
                tickets.size(),
                totalAmount,
                ticketListHtml.toString()
        );
    }

    private String loadEmailTemplate() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("app/templates/email/ticket-confirmation.html")) {
            if (is != null) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
            throw new IOException("Template not found in resources");
        } catch (IOException e) {
            return """
                    <!DOCTYPE html>
                    <html>
                    <body>
                        <h2 style="color: #2c3e50;">🎟️ Your Ticket Confirmation </h2>
                        <p>Thank you for purchasing tickets for <strong>%s</strong>!</p>
                        
                        <div style="border: 1px solid #e0e0e0; padding: 20px; margin: 20px 0; border-radius: 8px;">
                            <h3 style="color: #2c3e50; margin-top: 0;">Event Overview</h3>
                            <p>📅 Date & Time: %s</p>
                            <p>📍 Venue: %s</p>
                            <p>🏙️ City: %s</p>
                            <p>🔢 Total Tickets: %d</p>
                            <p>💵 Total Paid: $%.2f</p>
                            
                            <h3 style="color: #2c3e50;">Ticket Breakdown</h3>
                            %s
                        </div>
                        
                        <p style="color: #7f8c8d; font-size: 0.9em;">Have a great experience at the event!</p>
                    </body>
                    </html>""";
        }
    }

    /**
     * Get all tickets purchased by a user
     * 
     * @param customerEmail the email of the customer
     * @return list of ticket responses with event details
     */
    public List<MyTicketResponse> getMyTickets(String customerEmail) {
        var tickets = ticketTypeRepository.findByCustomerEmailOrderByCreatedDateDesc(customerEmail);
        
        return tickets.stream()
                .map(this::mapToMyTicketResponse)
                .toList();
    }

    /**
     * Maps a TicketType entity to MyTicketResponse DTO
     * Handles null event gracefully by extracting event details safely
     * 
     * @param ticket the ticket entity to map
     * @return MyTicketResponse with ticket and event details
     */
    private MyTicketResponse mapToMyTicketResponse(TicketType ticket) {
        Event event = ticket.getEvent();

        Long eventId = event != null ? event.getId() : null;
        String eventName = event != null ? event.getName() : null;
        java.time.LocalDateTime eventDateTime = event != null ? event.getDateTime() : null;
        String venue = event != null ? event.getVenue() : null;
        String city = event != null ? event.getCity() : null;
        
        return new MyTicketResponse(
                ticket.getId(),
                ticket.getCategory(),
                ticket.getPrice(),
                ticket.isRedeemed(),
                ticket.getQrCodePath(),
                eventId,
                eventName,
                eventDateTime,
                venue,
                city,
                ticket.getCreatedDate()
        );
    }
}
