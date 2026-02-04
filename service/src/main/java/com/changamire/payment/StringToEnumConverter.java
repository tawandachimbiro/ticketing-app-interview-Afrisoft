package com.changamire.payment;

import org.springframework.core.convert.converter.Converter;

/**
 * String to Enum Converter (Base Class)
 * 
 * This generic converter transforms string values to enum types, handling
 * case-insensitive conversion for Spring request parameters.
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public class StringToEnumConverter<T extends Enum<T>> implements Converter<String, T> {
    private final Class<T> enumType;

    public StringToEnumConverter(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public T convert(String source) {
        if (source.isEmpty()) {
            return null;
        }
        return Enum.valueOf(enumType, source.toUpperCase());
    }
}