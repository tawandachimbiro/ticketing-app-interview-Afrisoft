package com.changamire.payment;


import com.changamire.base.AbstractAuditingEntity;
import com.changamire.enums.Currency;
import com.changamire.enums.PaymentMethod;
import com.changamire.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Transaction Entity
 * 
 * This entity represents a payment transaction in the system including
 * transaction details, payment method, status, and associated URLs for
 * payment processing and callbacks.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Transaction extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transactionId;
    private String reference;
    @Enumerated(EnumType.STRING)
    private Status status;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private String email;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Column(name = "success_url")
    private String successUrl;

    @Column(name = "failure_url")
    private String failureUrl;

    @Column(name = "hosted_url")
    private String hostedUrl;

    @Column(name = "checkout_id")
    private String checkoutId;

}
