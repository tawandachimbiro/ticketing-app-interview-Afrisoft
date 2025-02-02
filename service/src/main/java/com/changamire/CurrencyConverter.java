package com.changamire;

import org.springframework.stereotype.Component;

@Component
public class CurrencyConverter extends StringToEnumConverter<Currency> {
    public CurrencyConverter() {
        super(Currency.class);
    }
}
