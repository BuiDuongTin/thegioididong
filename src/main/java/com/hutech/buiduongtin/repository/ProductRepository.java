package com.hutech.buiduongtin.repository;

import com.hutech.buiduongtin.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Lấy sản phẩm khuyến mãi
    List<Product> findByPromotionTypeIn(List<String> promotionTypes);

    // Lọc theo danh mục (cấp 1 hoặc cấp 2)
    List<Product> findByCategoryId(Long categoryId);

    // Lọc theo danh mục cha (hiển thị tất cả sản phẩm thuộc nhóm lớn)
    @Query("SELECT p FROM Product p WHERE p.category.id = :catId OR p.category.parentCategory.id = :catId")
    List<Product> findByCategoryIdOrParentId(@Param("catId") Long categoryId);
}