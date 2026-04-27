package com.hutech.buiduongtin.api.controller;

import com.hutech.buiduongtin.api.dto.response.ApiResponse;
import com.hutech.buiduongtin.api.dto.response.PagedResponse;
import com.hutech.buiduongtin.api.dto.response.ProductDetailResponse;
import com.hutech.buiduongtin.api.dto.response.ProductResponse;
import com.hutech.buiduongtin.api.mapper.ProductApiMapper;
import com.hutech.buiduongtin.api.util.ApiSortUtils;
import com.hutech.buiduongtin.model.Product;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ApiProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<PagedResponse<ProductResponse>> getProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean promotionOnly,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {
        String normalizedKeyword = keyword == null ? null : keyword.trim();
        String normalizedSort = ApiSortUtils.normalizeProductSort(sort);
        PageRequest pageRequest = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), 100),
                ApiSortUtils.parseProductSort(sort));

        Page<Product> productPage = productService.searchProducts(
                normalizedKeyword,
                categoryId,
                minPrice,
                maxPrice,
                promotionOnly,
                inStock,
                pageRequest);

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

        return ApiResponse.success("Products fetched", response);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        return ApiResponse.success("Product fetched", ProductApiMapper.toResponse(product));
    }

    @GetMapping("/{id}/detail")
    public ApiResponse<ProductDetailResponse> getProductDetail(
            @PathVariable Long id,
            @RequestParam(defaultValue = "6") int relatedLimit) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        List<Product> relatedProducts = productService.getRelatedProducts(id, categoryId, relatedLimit);

        return ApiResponse.success("Product detail fetched", ProductApiMapper.toDetailResponse(product, relatedProducts));
    }
}
