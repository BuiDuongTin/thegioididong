package com.hutech.buiduongtin.service;

import com.hutech.buiduongtin.model.Category;
import com.hutech.buiduongtin.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@SuppressWarnings("null")
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ImageStorageService imageStorageService;

    // Lấy tất cả danh mục
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Lấy danh mục cấp 1 (kèm danh mục con do EAGER loading)
    @Cacheable("categoryTree")
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentCategoryIsNull();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public List<Category> getChildrenByParentId(Long parentId) {
        return categoryRepository.findByParentCategoryId(parentId);
    }

    @CacheEvict(value = "categoryTree", allEntries = true)
    public Category saveCategory(Category category, org.springframework.web.multipart.MultipartFile imageFile)
            throws java.io.IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageFileName = imageStorageService.store(imageFile);
            category.setImage(imageFileName);
        }
        return categoryRepository.save(category);
    }

    @CacheEvict(value = "categoryTree", allEntries = true)
    public Category updateCategory(Category category, org.springframework.web.multipart.MultipartFile imageFile)
            throws java.io.IOException {
        Category existingCategory = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new IllegalStateException("Category not found"));

        existingCategory.setName(category.getName());
        existingCategory.setIcon(category.getIcon());
        existingCategory.setParentCategory(category.getParentCategory());

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageFileName = imageStorageService.store(imageFile);
            existingCategory.setImage(imageFileName);
        }

        return categoryRepository.save(existingCategory);
    }

    @CacheEvict(value = "categoryTree", allEntries = true)
    public void deleteCategoryById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalStateException("Category not found");
        }
        categoryRepository.deleteById(id);
    }
}
