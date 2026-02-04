package com.changamire.payment;

import com.changamire.enums.Currency;
import com.changamire.enums.PaymentMethod;
import com.changamire.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

/**
 * Transaction Repository
 * 
 * This repository handles payment transaction data access including lookups by
 * reference, status, payment method, and currency. Supports paginated queries.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByReference(String reference);

    Page<Transaction> findAllByStatus(Status status, Pageable pageable);

    Page<Transaction> findAllByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);

    Page<Transaction> findAllByCurrency(Currency currency, Pageable pageable);

}