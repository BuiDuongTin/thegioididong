package com.hutech.buiduongtin.api.dto.response;

import java.util.List;

public record ProductDetailResponse(
        ProductResponse product,
        CategoryResponse category,
        List<CategoryBreadcrumbResponse> breadcrumbs,
        List<ProductResponse> relatedProducts) {
}
