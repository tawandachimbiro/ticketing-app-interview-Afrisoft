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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
@Service
@RequiredArgsConstructor
public class TicketPurchaseService {

    private final EventRepository eventRepository;
    private final PaymentService paymentService;
    private final CardPaymentService cardPaymentService;
    private final EmailService emailService;
    private final TicketTypeRepository ticketTypeRepository;
    private final QRCodeService qrCodeService; // QR Code Service added

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

                // Trigger confirmation email asynchronously (does NOT block payment)
                System.out.println("=== QUEUING ASYNC EMAIL SEND ===");
                System.out.println("To: " + request.customerEmail());
                System.out.println("Subject: Your Ticket Confirmation - " + event.getName());

                emailService.sendTicketConfirmation(
                        request.customerEmail(),
                        "Your Ticket Confirmation - " + event.getName(),
                        ticketDetails
                );

                System.out.println("✅ Email send requested (async) for: " + request.customerEmail());

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

    // Helper methods remain the same
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
                    .filter(tt -> tt.getQrCodePath() == null) // Only get template tickets (not purchased ones)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Ticket type not available: " + ticketRequest.category()));

            for (int i = 0; i < ticketRequest.quantity(); i++) {
                var ticket = new TicketType();
                ticket.setCategory(ticketTemplate.getCategory());
                ticket.setPrice(ticketTemplate.getPrice());
                ticket.setEvent(event);  // Keep event relationship for validation
                ticketTypeRepository.save(ticket); // Save first to generate ID

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
                    ticketTypeRepository.save(ticket); // Update with QR code path
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

    // Modified to show QR codes instead of ticket IDs
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
}







//package com.changamire.event;
//
//import com.changamire.enums.PaymentMethod;
//import com.changamire.enums.TicketCategory;
//import com.changamire.exceptions.EventNotFoundException;
//import com.changamire.exceptions.ExternalServiceUnavailableException;
//import com.changamire.exceptions.PaymentProcessingException;
//import com.changamire.exceptions.TicketsSoldOutException;
//import com.changamire.payment.*;
//import com.changamire.enums.Currency;
//import com.changamire.enums.Status;
//import com.changamire.ticket.TicketType;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class TicketPurchaseService {
//
//    private final EventRepository eventRepository;
//    private final PaymentService paymentService;
//    private final CardPaymentService cardPaymentService;
//    private final EmailService emailService;
//    private final TicketTypeRepository ticketTypeRepository;
//    private final QRCodeService qrCodeService;
//
//    @Transactional
//    public TicketPurchaseResponse purchaseTicket(TicketPurchaseRequest request) {
//        Event event = eventRepository.findById(request.getEventId())
//                .orElseThrow(() -> new EventNotFoundException("Event not found"));
//
//        // Calculate total tickets requested
//        int totalTickets = request.getTickets().stream()
//                .mapToInt(TicketTypeQuantity::getQuantity)
//                .sum();
//
//        // Validate capacity
//        if (event.getCapacity() < totalTickets) {
//            throw new TicketsSoldOutException("Not enough tickets available");
//        }
//
//        try {
//            // Calculate total amount
//            double totalAmount = calculateTotalAmount(event, request.getTickets());
//
//            // Process payment with calculated amount
//            Object paymentResponse = processPayment(event, request, totalAmount);
//
//            if (isPaymentSuccessful(paymentResponse)) {
//                List<TicketType> purchasedTickets = generateAndPersistTickets(event, request);
//                updateEventCapacity(event, totalTickets);
//                String ticketDetails = generateTicketDetails(event, request, purchasedTickets, paymentResponse, totalAmount);
//
//                emailService.sendTicketConfirmation(
//                        request.getCustomerEmail(),
//                        "Your Ticket Confirmation - " + event.getName(),
//                        ticketDetails
//                );
//
//                return new TicketPurchaseResponse(
//                        true,
//                        "Purchase successful",
//                        getTransactionId(paymentResponse),
//                        ticketDetails,
//                        getCode(paymentResponse),          // New field
//                        request.getPaymentMethod(),        // From request
//                        getHostedUrl(paymentResponse),     // New field
//                        getCheckoutId(paymentResponse)     // New field
//                );
//            }
//            // Update failure response with new fields
//            return new TicketPurchaseResponse(
//                    false,
//                    "Payment failed",
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//
//        } catch (PaymentProcessingException | ExternalServiceUnavailableException e) {
//            return new TicketPurchaseResponse(
//                    false,
//                    e.getMessage(),
//                    null,
//                    null,
//                    null,
//                    null,
//                    null,
//                    null
//            );
//        }
//    }
//
//
//    // Helper methods to extract new fields from payment response
//    private String getCode(Object response) {
//        if (response instanceof PaymentResponse pr) return pr.getCode();
//       // if (response instanceof CardPaymentResponse cpr) return cpr.getReference();
//        return null;
//    }
//
//    private String getHostedUrl(Object response) {
//       // if (response instanceof PaymentResponse pr) return pr.getHostedUrl();
//        if (response instanceof CardPaymentResponse cpr) return cpr.getHostedUrl();
//        return null;
//    }
//
//    private String getCheckoutId(Object response) {
//      //  if (response instanceof PaymentResponse pr) return pr.getCheckoutId();
//        if (response instanceof CardPaymentResponse cpr) return cpr.getCheckoutId();
//        return null;
//    }
//
//    private Object processPayment(Event event, TicketPurchaseRequest request, double totalAmount) {
//        if (isCardPayment(request.getPaymentMethod())) {
//            return processCardPayment(event, request, totalAmount);
//        } else {
//            return processMobileMoneyPayment(event, request, totalAmount);
//        }
//    }
//
//    private boolean isCardPayment(PaymentMethod method) {
//        return method == PaymentMethod.ZIMSWITCH || method == PaymentMethod.INTERNATIONAL_CARD;
//    }
//
//    private CardPaymentResponse processCardPayment(Event event, TicketPurchaseRequest request, double totalAmount) {
//        CardPaymentRequest cardRequest = new CardPaymentRequest();
//        cardRequest.setAmount(totalAmount);
//        cardRequest.setEmail(request.getCustomerEmail());
//        cardRequest.setCurrency(Currency.USD);
//
//        return cardPaymentService.processCardPayment(cardRequest, request.getPaymentMethod());
//    }
//
//    private PaymentResponse processMobileMoneyPayment(Event event, TicketPurchaseRequest request, double totalAmount) {
//        PaymentRequest paymentRequest = new PaymentRequest();
//        paymentRequest.setAmount(totalAmount);
//        paymentRequest.setEmail(request.getCustomerEmail());
//        paymentRequest.setMobileMoneyNumber(request.getMobileNumber());
//        paymentRequest.setPaymentMethod(request.getPaymentMethod());
//        paymentRequest.setCurrency(Currency.USD);
//
//        return paymentService.processPayment(paymentRequest);
//    }
//
//    private List<TicketType> generateAndPersistTickets(Event event, TicketPurchaseRequest request) {
//        List<TicketType> tickets = new ArrayList<>();
//
//        request.getTickets().forEach(ticketRequest -> {
//            TicketType ticketType = event.getTicketTypes().stream()
//                    .filter(tt -> tt.getCategory() == ticketRequest.getCategory())
//                    .findFirst()
//                    .orElseThrow(() -> new IllegalArgumentException("Ticket type not available: " + ticketRequest.getCategory()));
//
//            for (int i = 0; i < ticketRequest.getQuantity(); i++) {
//                TicketType ticket = new TicketType();
//                ticket.setCategory(ticketType.getCategory());
//                ticket.setPrice(ticketType.getPrice());
//                ticket.setEvent(event);
//                ticketTypeRepository.save(ticket);
//                tickets.add(ticket);
//            }
//        });
//
//        return tickets;
//    }
//
//    private void updateEventCapacity(Event event, int totalTickets) {
//        event.setCapacity(event.getCapacity() - totalTickets);
//        eventRepository.save(event);
//    }
//
//    private boolean isPaymentSuccessful(Object paymentResponse) {
//        return (paymentResponse instanceof PaymentResponse pr && pr.getStatus() == Status.SUCCESS) ||
//                (paymentResponse instanceof CardPaymentResponse cpr && cpr.getResult().equalsIgnoreCase("success"));
//    }
//
//    private String getTransactionId(Object response) {
//        if (response instanceof PaymentResponse pr) return pr.getTransactionId();
//        if (response instanceof CardPaymentResponse cpr) return cpr.getTransactionId();
//        return null;
//    }
//
//    private Double calculateTotalAmount(Event event, List<TicketTypeQuantity> tickets) {
//        return tickets.stream()
//                .mapToDouble(tq -> {
//                    TicketType tt = event.getTicketTypes().stream()
//                            .filter(t -> t.getCategory() == tq.getCategory())
//                            .findFirst()
//                            .orElseThrow(() -> new IllegalStateException("Ticket type not found"));
//                    return tt.getPrice() * tq.getQuantity();
//                })
//                .sum();
//    }
//
//    private String generateTicketDetails(Event event,
//                                         TicketPurchaseRequest request,
//                                         List<TicketType> tickets,
//                                         Object paymentResponse,
//                                         double totalAmount) {
//        // Group tickets by category
//        Map<TicketCategory, List<TicketType>> ticketsByCategory = tickets.stream()
//                .collect(Collectors.groupingBy(TicketType::getCategory));
//
//        // Build ticket list HTML
//        StringBuilder ticketListHtml = new StringBuilder();
//        ticketsByCategory.forEach((category, ticketList) -> {
//            int quantity = ticketList.size();
//            double price = ticketList.get(0).getPrice();
//            double subtotal = price * quantity;
//
//            String ticketIds = ticketList.stream()
//                    .map(t -> String.format("<li style='margin-bottom: 5px;'>🎫 Ticket ID: <strong>%s</strong></li>", t.getId()))
//                    .collect(Collectors.joining());
//
//            ticketListHtml.append(String.format(
//                    "<div style='margin-bottom: 20px; border-bottom: 1px solid #eee; padding-bottom: 15px;'>" +
//                            "<h4 style='color: #2c3e50; margin-top: 0;'>%s Tickets</h4>" +
//                            "<p>Quantity: %d</p>" +
//                            "<p>Price per ticket: $%.2f</p>" +
//                            "<p>Subtotal: $%.2f</p>" +
//                            "<ul style='list-style: none; padding-left: 0; margin-top: 10px;'>%s</ul>" +
//                            "</div>",
//                    category, quantity, price, subtotal, ticketIds
//            ));
//        });
//
//        return String.format(
//                loadEmailTemplate(),
//                event.getName(),
//                event.getDateTime().format(DateTimeFormatter.ofPattern("EEE, MMM dd yyyy hh:mm a")),
//                event.getVenue(),
//                event.getCity(),
//                tickets.size(),  // Total tickets
//                totalAmount,    // Total paid
//                ticketListHtml.toString()
//        );
//    }
//
//    private String loadEmailTemplate() {
//        try (InputStream is = getClass().getClassLoader().getResourceAsStream("app/templates/email/ticket-confirmation.html")) {
//            if (is != null) {
//                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
//            } else {
//                throw new IOException("Template not found in resources");
//            }
//        } catch (IOException e) {
//            // Fallback template
//            return """
//                    <!DOCTYPE html>
//                    <html>
//                    <body>
//                        <h2 style="color: #2c3e50;">🎟️ Your Ticket Confirmation </h2>
//                        <p>Thank you for purchasing tickets for <strong>%s</strong>!</p>
//
//                        <div style="border: 1px solid #e0e0e0; padding: 20px; margin: 20px 0; border-radius: 8px;">
//                            <h3 style="color: #2c3e50; margin-top: 0;">Event Overview</h3>
//                            <p>📅 Date & Time: %s</p>
//                            <p>📍 Venue: %s</p>
//                            <p>🏙️ City: %s</p>
//                            <p>🔢 Total Tickets: %d</p>
//                            <p>💵 Total Paid: $%.2f</p>
//
//                            <h3 style="color: #2c3e50;">Ticket Breakdown</h3>
//                            %s
//                        </div>
//
//                        <p style="color: #7f8c8d; font-size: 0.9em;">Have a great experience at the event!</p>
//                    </body>
//                    </html>""";
//        }
//    }
//}