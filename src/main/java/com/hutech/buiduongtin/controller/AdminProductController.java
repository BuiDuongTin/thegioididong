package com.hutech.buiduongtin.controller;

import com.hutech.buiduongtin.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @GetMapping
    public String listProductsAdmin(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products/list";
    }
}
