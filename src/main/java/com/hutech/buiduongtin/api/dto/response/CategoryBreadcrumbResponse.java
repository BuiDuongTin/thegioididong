package com.hutech.buiduongtin.api.dto.response;

public record CategoryBreadcrumbResponse(
        Long id,
        String name,
        Long parentId) {
}
