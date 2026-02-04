package com.changamire.payment;

import com.changamire.enums.Currency;
import org.springframework.stereotype.Component;

/**
 * Currency Converter
 * <p>
 * This Spring converter component handles conversion of string values
 * to Currency enum constants for request parameter binding.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
@Component
public class CurrencyConverter extends StringToEnumConverter<Currency> {
    public CurrencyConverter() {
        super(Currency.class);
    }
}
