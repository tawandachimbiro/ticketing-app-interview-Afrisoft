package com.changamire.enums;


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

