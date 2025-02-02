package com.changamire.payment;

import com.changamire.enums.Currency;
import org.springframework.stereotype.Component;

@Component
public class CurrencyConverter extends StringToEnumConverter<Currency> {
    public CurrencyConverter() {
        super(Currency.class);
    }
}
