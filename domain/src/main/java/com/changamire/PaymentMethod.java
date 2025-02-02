package com.changamire;


public enum PaymentMethod {
    ECOCASH("mobile_money", "ecocash"),
    INNBUCKS("mobile_money", "innbucks");

    private final String type;
    private final String code;

    PaymentMethod(String type, String code) {
        this.type = type;
        this.code = code;
    }

    public String getType() { return type; }
    public String getCode() { return code; }
}