package com.hutech.buiduongtin.controller;

import com.hutech.buiduongtin.service.CategoryService;
import com.hutech.buiduongtin.service.ProductService;
import com.hutech.buiduongtin.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService; // Inject CategoryService

    @GetMapping("")
    public String listProducts(@RequestParam(value = "categoryId", required = false) Long categoryId,
            Model model) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        model.addAttribute("products", products);
        model.addAttribute("promotionProducts", productService.getPromotionProducts());
        model.addAttribute("selectedCategoryId", categoryId);
        return "products/product-list";
    }

    @GetMapping("/detail/{id}")
    public String viewProductDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        return "products/product-detail";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories()); // Load categories
        return "products/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile) {
        if (result.hasErrors()) {
            return "products/add-product";
        }
        try {
            productService.addProduct(product, imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return "products/add-product";
        }
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories()); // Load categories
        return "products/add-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product, BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile) {
        if (result.hasErrors()) {
            product.setId(id);
            return "products/add-product";
        }
        try {
            productService.updateProduct(product, imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return "products/add-product";
        }
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        try {
            productService.deleteProductById(id);
            redirectAttrs.addFlashAttribute("message", "Đã xóa sản phẩm thành công!");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttrs.addFlashAttribute("error", "Không thể xóa sản phẩm này vì đã có trong đơn mua hàng!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Có lỗi xảy ra khi xóa sản phẩm: " + e.getMessage());
        }
        return "redirect:/products";
    }
}
