package com.changamire.ticket;

import com.changamire.ticket.TicketTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketTransactionRepository extends JpaRepository<TicketTransaction, Long> {
}
