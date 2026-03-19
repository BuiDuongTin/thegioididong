package com.hutech.buiduongtin.repository;

import com.hutech.buiduongtin.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Lấy danh mục cấp 1 (không có cha)
    List<Category> findByParentCategoryIsNull();
}
