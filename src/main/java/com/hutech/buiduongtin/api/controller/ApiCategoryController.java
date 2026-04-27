package com.hutech.buiduongtin.api.controller;

import com.hutech.buiduongtin.api.dto.response.ApiResponse;
import com.hutech.buiduongtin.api.dto.response.CategoryResponse;
import com.hutech.buiduongtin.api.dto.response.CategoryTreeResponse;
import com.hutech.buiduongtin.api.dto.response.PagedResponse;
import com.hutech.buiduongtin.api.dto.response.ProductResponse;
import com.hutech.buiduongtin.api.mapper.CategoryApiMapper;
import com.hutech.buiduongtin.api.mapper.ProductApiMapper;
import com.hutech.buiduongtin.api.util.ApiSortUtils;
import com.hutech.buiduongtin.model.Category;
import com.hutech.buiduongtin.model.Product;
import com.hutech.buiduongtin.service.CategoryService;
import com.hutech.buiduongtin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class ApiCategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getCategories(@RequestParam(defaultValue = "false") boolean rootOnly) {
        List<Category> categories = rootOnly ? categoryService.getRootCategories() : categoryService.getAllCategories();
        List<CategoryResponse> data = categories.stream().map(CategoryApiMapper::toResponse).toList();
        return ApiResponse.success("Categories fetched", data);
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategory(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        return ApiResponse.success("Category fetched", CategoryApiMapper.toResponse(category));
    }

    @GetMapping("/tree")
    public ApiResponse<List<CategoryTreeResponse>> getCategoryTree() {
        List<CategoryTreeResponse> tree = categoryService.getRootCategories().stream()
                .map(CategoryApiMapper::toTreeResponse)
                .toList();
        return ApiResponse.success("Category tree fetched", tree);
    }

    @GetMapping("/{id}/children")
    public ApiResponse<List<CategoryResponse>> getCategoryChildren(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }
        List<CategoryResponse> children = categoryService.getChildrenByParentId(id).stream()
                .map(CategoryApiMapper::toResponse)
                .toList();
        return ApiResponse.success("Category children fetched", children);
    }

    @GetMapping("/{id}/products")
    public ApiResponse<PagedResponse<ProductResponse>> getCategoryProducts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }

        String normalizedSort = ApiSortUtils.normalizeProductSort(sort);
        PageRequest pageRequest = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                ApiSortUtils.parseProductSort(sort));
        Page<Product> productPage = productService.getProductsByCategory(id, pageRequest);
        List<ProductResponse> items = productPage.getContent().stream()
                .map(ProductApiMapper::toResponse)
                .toList();

        PagedResponse<ProductResponse> response = new PagedResponse<>(
                items,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isFirst(),
                productPage.isLast(),
                productPage.hasNext(),
                productPage.hasPrevious(),
                productPage.hasNext() ? productPage.getNumber() + 1 : null,
                productPage.hasPrevious() ? productPage.getNumber() - 1 : null,
                normalizedSort);
        return ApiResponse.success("Category products fetched", response);
    }
}
