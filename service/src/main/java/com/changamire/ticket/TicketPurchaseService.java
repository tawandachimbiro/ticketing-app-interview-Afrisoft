//package com.changamire.ticket;
//
//import com.changamire.bus.BusSchedule;
//import com.changamire.customer.Customer;
//import com.changamire.customer.CustomerRepository;
//import com.changamire.ticket.TicketTransactionRepository;
//import com.changamire.bus.BusScheduleRepository;
//import com.changamire.ticket.TicketPurchaseDTO;
//import com.changamire.ticket.TicketTransaction;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalTime;
//
//@Service
//@RequiredArgsConstructor
//public class TicketPurchaseService {
//
//    @Autowired
//    private CustomerRepository customerRepository;
//
//    @Autowired
//    private BusScheduleRepository busScheduleRepository;
//
//    @Autowired
//    private TicketTransactionRepository transactionRepository;
//
//    @Transactional
//    public TicketTransaction purchaseTicket(TicketPurchaseDTO purchaseDTO) {
//        // Find or create customer
//        Customer customer = customerRepository.findByEmail(purchaseDTO.getCustomerEmail())
//                .orElseGet(() -> {
//                    Customer newCustomer = new Customer();
//                    newCustomer.setEmail(purchaseDTO.getCustomerEmail());
//                    return customerRepository.save(newCustomer);
//                });
//
//        // Find bus schedule
//        BusSchedule schedule = (BusSchedule) busScheduleRepository.findByTicketId(purchaseDTO.getTicketId())
//                .orElseThrow(() -> new RuntimeException("Invalid ticket ID"));
//
//        // Check seat availability
//        if (schedule.getAvailableSeats() < purchaseDTO.getQuantity()) {
//            throw new RuntimeException("Not enough seats available");
//        }
//
//        // Update available seats
//        schedule.setAvailableSeats(schedule.getAvailableSeats() - purchaseDTO.getQuantity());
//        busScheduleRepository.save(schedule);
//
//        // Create transaction
//        TicketTransaction transaction = new TicketTransaction();
//        transaction.setPurchaseDate(LocalDate.now());
//        transaction.setPurchaseTime(LocalTime.now());
//        transaction.setTotalAmount(
//                BigDecimal.valueOf(schedule.getBusRoute().getTicketPrice().doubleValue() * purchaseDTO.getQuantity())
//        );
//        transaction.setBusSchedule(schedule);
//        transaction.setCustomer(customer);
//        transaction.setPaymentMethod(purchaseDTO.getPaymentMethod());
//        transaction.setPaymentStatus("COMPLETED");
//
//        return transactionRepository.save(transaction);
//    }
//}