package com.hutech.buiduongtin.controller;

import com.hutech.buiduongtin.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import com.hutech.buiduongtin.model.Category;

/**
 * Tự động inject danh mục vào tất cả view (layout.html dùng sec:authorize)
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final CategoryService categoryService;
    private final com.hutech.buiduongtin.service.CartService cartService;

    @ModelAttribute("rootCategories")
    public List<Category> rootCategories() {
        return categoryService.getRootCategories();
    }

    @ModelAttribute("cartCount")
    public int cartCount() {
        return cartService.getCartCount();
    }
}
