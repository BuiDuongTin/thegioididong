package com.hutech.buiduongtin.api.dto.response;

import java.util.List;

public record CategoryTreeResponse(
        Long id,
        String name,
        String icon,
        String image,
        Long parentId,
        int productCount,
        List<CategoryTreeResponse> children) {
}
