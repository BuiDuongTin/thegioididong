package com.hutech.buiduongtin.model.enums;

import java.util.Arrays;

public enum PaymentMethod {
    COD,
    BANK_TRANSFER,
    MOMO,
    VOUCHER;

    public String code() {
        return name();
    }

    public static PaymentMethod fromCode(String value) {
        if (value == null || value.isBlank()) {
            return COD;
        }
        return Arrays.stream(values())
                .filter(method -> method.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(COD);
    }
}
