package com.hutech.buiduongtin.api.mapper;

import com.hutech.buiduongtin.api.dto.response.CategoryBreadcrumbResponse;
import com.hutech.buiduongtin.api.dto.response.CategoryResponse;
import com.hutech.buiduongtin.api.dto.response.ProductDetailResponse;
import com.hutech.buiduongtin.api.dto.response.ProductResponse;
import com.hutech.buiduongtin.model.Category;
import com.hutech.buiduongtin.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ProductApiMapper {

    private ProductApiMapper() {
    }

    public static ProductResponse toResponse(Product product) {
        Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getRealPrice(),
                product.getDescription(),
                product.getImage(),
                product.getPromotionType(),
                product.getDiscountPercent(),
                product.getGiftDescription(),
                product.getStockQuantity(),
                categoryId,
                categoryName);
    }

    public static ProductDetailResponse toDetailResponse(Product product, List<Product> relatedProducts) {
        CategoryResponse category = product.getCategory() == null ? null : CategoryApiMapper.toResponse(product.getCategory());
        List<CategoryBreadcrumbResponse> breadcrumbs = buildBreadcrumbs(product.getCategory());
        List<ProductResponse> related = relatedProducts == null
                ? List.of()
                : relatedProducts.stream().map(ProductApiMapper::toResponse).toList();
        return new ProductDetailResponse(toResponse(product), category, breadcrumbs, related);
    }

    private static List<CategoryBreadcrumbResponse> buildBreadcrumbs(Category category) {
        if (category == null) {
            return List.of();
        }

        List<CategoryBreadcrumbResponse> breadcrumbs = new ArrayList<>();
        Category current = category;
        while (current != null) {
            Long parentId = current.getParentCategory() != null ? current.getParentCategory().getId() : null;
            breadcrumbs.add(new CategoryBreadcrumbResponse(current.getId(), current.getName(), parentId));
            current = current.getParentCategory();
        }
        Collections.reverse(breadcrumbs);
        return breadcrumbs;
    }
}
