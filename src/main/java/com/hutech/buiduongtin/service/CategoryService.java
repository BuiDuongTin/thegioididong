package com.hutech.buiduongtin.service;

import com.hutech.buiduongtin.model.Category;
import com.hutech.buiduongtin.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@SuppressWarnings("null")
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // Lấy tất cả danh mục
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Lấy danh mục cấp 1 (kèm danh mục con do EAGER loading)
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentCategoryIsNull();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category saveCategory(Category category, org.springframework.web.multipart.MultipartFile imageFile)
            throws java.io.IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageFileName = saveImage(imageFile);
            category.setImage(imageFileName);
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Category category, org.springframework.web.multipart.MultipartFile imageFile)
            throws java.io.IOException {
        Category existingCategory = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new IllegalStateException("Category not found"));

        existingCategory.setName(category.getName());
        existingCategory.setIcon(category.getIcon());
        existingCategory.setParentCategory(category.getParentCategory());

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageFileName = saveImage(imageFile);
            existingCategory.setImage(imageFileName);
        }

        return categoryRepository.save(existingCategory);
    }

    public void deleteCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalStateException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    private String saveImage(org.springframework.web.multipart.MultipartFile imageFile) throws java.io.IOException {
        String uploadDir = "src/main/resources/static/images/";
        java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);

        if (!java.nio.file.Files.exists(uploadPath)) {
            java.nio.file.Files.createDirectories(uploadPath);
        }

        String fileName = java.util.UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
        java.nio.file.Path filePath = uploadPath.resolve(fileName);

        java.nio.file.Files.copy(imageFile.getInputStream(), filePath,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}
