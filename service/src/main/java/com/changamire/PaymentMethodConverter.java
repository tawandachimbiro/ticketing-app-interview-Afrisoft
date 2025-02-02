package com.changamire;

import org.springframework.stereotype.Component;

@Component
public class PaymentMethodConverter extends StringToEnumConverter<PaymentMethod> {
    public PaymentMethodConverter() {
        super(PaymentMethod.class);
    }
}