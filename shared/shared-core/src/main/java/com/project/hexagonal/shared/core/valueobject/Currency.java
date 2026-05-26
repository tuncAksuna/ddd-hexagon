package com.project.hexagonal.shared.core.valueobject;

public enum Currency {
    TRY("TRY"),
    USD("USD"),
    EUR("EUR");

    Currency(String val) {
        this.val = val;
    }

    private final String val;

    public String getVal() {
        return val;
    }
}
