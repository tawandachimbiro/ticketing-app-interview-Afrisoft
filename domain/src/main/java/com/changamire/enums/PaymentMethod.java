package com.changamire.enums;

/**
 * Payment Method Enum
 * <p>
 * This enum defines available payment methods in the ticketing system
 * including mobile money (EcoCash, Innbucks) and card payments
 * (ZIMSWITCH, international cards).
 * 
 * @author Archibold Chimbiro
 * @version 1.0.0
 * @since 2026-02-04
 */
public enum PaymentMethod {
    // Mobile Money
    ECOCASH("mobile_money", "ecocash"),
    INNBUCKS("mobile_money", "innbucks"),

    // Card Payments

    ZIMSWITCH("card", "zimswitch"),
    INTERNATIONAL_CARD("card", "international");

    private final String type;
    private final String code;

    PaymentMethod(String type, String code) {
        this.type = type;
        this.code = code;
    }

    public String getType() { return type; }
    public String getCode() { return code; }
}

