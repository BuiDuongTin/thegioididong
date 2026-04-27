package com.hutech.buiduongtin.api.mapper;

import com.hutech.buiduongtin.api.dto.response.CategoryResponse;
import com.hutech.buiduongtin.api.dto.response.CategoryTreeResponse;
import com.hutech.buiduongtin.model.Category;

import java.util.List;

public final class CategoryApiMapper {

    private CategoryApiMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        Long parentId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;
        int childrenCount = category.getChildren() == null ? 0 : category.getChildren().size();
        int productCount = category.getProducts() == null ? 0 : category.getProducts().size();

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getIcon(),
                category.getImage(),
                parentId,
                childrenCount,
                productCount);
    }

    public static CategoryTreeResponse toTreeResponse(Category category) {
        Long parentId = category.getParentCategory() != null ? category.getParentCategory().getId() : null;
        int productCount = category.getProducts() == null ? 0 : category.getProducts().size();
        List<CategoryTreeResponse> children = category.getChildren() == null
                ? List.of()
                : category.getChildren().stream().map(CategoryApiMapper::toTreeResponse).toList();

        return new CategoryTreeResponse(
                category.getId(),
                category.getName(),
                category.getIcon(),
                category.getImage(),
                parentId,
                productCount,
                children);
    }
}
