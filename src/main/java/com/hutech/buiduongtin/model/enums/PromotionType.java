package com.hutech.buiduongtin.model.enums;

import java.util.Arrays;

public enum PromotionType {
    NONE,
    DISCOUNT,
    GIFT;

    public String code() {
        return name();
    }

    public static PromotionType fromCode(String value) {
        if (value == null || value.isBlank()) {
            return NONE;
        }
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(NONE);
    }

    public boolean isActive() {
        return this == DISCOUNT || this == GIFT;
    }
}
