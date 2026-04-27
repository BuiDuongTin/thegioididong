package com.hutech.buiduongtin.api.dto.response;

public record ProductResponse(
        Long id,
        String name,
        double price,
        double realPrice,
        String description,
        String image,
        String promotionType,
        int discountPercent,
        String giftDescription,
        int stockQuantity,
        Long categoryId,
        String categoryName) {
}
