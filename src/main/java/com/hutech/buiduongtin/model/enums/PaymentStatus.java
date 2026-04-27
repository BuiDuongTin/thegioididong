package com.hutech.buiduongtin.model.enums;

import java.util.Arrays;

public enum PaymentStatus {
    PENDING,
    PAID,
    REDEEMED,
    FAILED;

    public String code() {
        return name();
    }

    public static PaymentStatus fromCode(String value) {
        if (value == null || value.isBlank()) {
            return PENDING;
        }
        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(PENDING);
    }
}
