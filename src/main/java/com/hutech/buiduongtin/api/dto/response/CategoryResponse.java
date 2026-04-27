package com.hutech.buiduongtin.api.dto.response;

public record CategoryResponse(
        Long id,
        String name,
        String icon,
        String image,
        Long parentId,
        int childrenCount,
        int productCount) {
}
