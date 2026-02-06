package com.changamire.payment;

import com.changamire.enums.PaymentMethod;
import org.springframework.stereotype.Component;

/**
 * Payment Method Converter
 * 
 * This Spring converter component handles conversion of string values
 * to PaymentMethod enum constants for request parameter binding.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Component
public class PaymentMethodConverter extends StringToEnumConverter<PaymentMethod> {
    public PaymentMethodConverter() {
        super(PaymentMethod.class);
    }
}