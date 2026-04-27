package com.hutech.buiduongtin.repository;

import com.hutech.buiduongtin.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    // Lấy sản phẩm khuyến mãi
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.promotionType IN :types")
    List<Product> findByPromotionTypeIn(@Param("types") List<String> promotionTypes);

    // Lọc theo danh mục (cấp 1 hoặc cấp 2) - with category fetch join
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.category.id = :catId")
    List<Product> findByCategoryId(@Param("catId") Long categoryId);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.category.id = :catId AND p.id != :excludedId")
    Page<Product> findByCategoryIdAndIdNot(@Param("catId") Long categoryId, @Param("excludedId") Long excludedId, Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.category.id = :catId")
    Page<Product> findByCategoryId(@Param("catId") Long categoryId, Pageable pageable);

    // Lọc theo danh mục cha (hiển thị tất cả sản phẩm thuộc nhóm lớn)
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.category.id = :catId OR p.category.parentCategory.id = :catId")
    List<Product> findByCategoryIdOrParentId(@Param("catId") Long categoryId);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.category.id = :catId OR p.category.parentCategory.id = :catId")
    Page<Product> findPageByCategoryIdOrParentId(@Param("catId") Long categoryId, Pageable pageable);

    // Get product by ID with category fetch (avoid N+1 when accessing category)
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);
}
