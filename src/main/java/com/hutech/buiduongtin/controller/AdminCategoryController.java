package com.hutech.buiduongtin.controller;

import com.hutech.buiduongtin.model.Category;
import com.hutech.buiduongtin.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.io.IOException;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("rootCategories", categoryService.getRootCategories());
        return "admin/categories/add";
    }

    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("category") Category category, BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("rootCategories", categoryService.getRootCategories());
            return "admin/categories/add";
        }
        try {
            categoryService.saveCategory(category, imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("rootCategories", categoryService.getRootCategories());
            return "admin/categories/add";
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) {
            return "redirect:/admin/categories";
        }
        model.addAttribute("category", category);
        model.addAttribute("rootCategories", categoryService.getRootCategories());
        return "admin/categories/edit";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id, @Valid @ModelAttribute("category") Category category,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile, Model model) {
        if (result.hasErrors()) {
            category.setId(id);
            model.addAttribute("rootCategories", categoryService.getRootCategories());
            return "admin/categories/edit";
        }
        try {
            categoryService.updateCategory(category, imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("rootCategories", categoryService.getRootCategories());
            return "admin/categories/edit";
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        try {
            categoryService.deleteCategoryById(id);
            redirectAttrs.addFlashAttribute("message", "Đã xóa danh mục thành công!");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttrs.addFlashAttribute("error",
                    "Không thể xóa danh mục này vì vẫn còn sản phẩm hoặc danh mục con bên trong!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Có lỗi xảy ra khi xóa danh mục: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}
