package com.changamire;


import com.changamire.enums.Currency;
import com.changamire.enums.PaymentMethod;
import com.changamire.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Transaction {
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



}
