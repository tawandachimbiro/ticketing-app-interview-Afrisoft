package com.changamire.payment;

import com.changamire.enums.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodConverter extends StringToEnumConverter<PaymentMethod> {
    public PaymentMethodConverter() {
        super(PaymentMethod.class);
    }
}