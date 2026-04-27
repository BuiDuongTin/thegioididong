package com.hutech.buiduongtin.repository.specification;

import com.hutech.buiduongtin.model.Product;
import com.hutech.buiduongtin.model.enums.PromotionType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> keyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            String like = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("description")), like));
        };
    }

    public static Specification<Product> categoryId(Long categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }
            Join<Object, Object> categoryJoin = root.join("category", JoinType.LEFT);
            Join<Object, Object> parentJoin = categoryJoin.join("parentCategory", JoinType.LEFT);
            return cb.or(
                    cb.equal(categoryJoin.get("id"), categoryId),
                    cb.equal(parentJoin.get("id"), categoryId));
        };
    }

    public static Specification<Product> minPrice(Double minPrice) {
        return (root, query, cb) -> minPrice == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> maxPrice(Double maxPrice) {
        return (root, query, cb) -> maxPrice == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Product> promotionOnly(Boolean promotionOnly) {
        return (root, query, cb) -> {
            if (promotionOnly == null) {
                return cb.conjunction();
            }
            if (!promotionOnly) {
                return cb.conjunction();
            }
            return cb.notEqual(root.get("promotionType"), PromotionType.NONE.code());
        };
    }

    public static Specification<Product> inStock(Boolean inStock) {
        return (root, query, cb) -> {
            if (inStock == null) {
                return cb.conjunction();
            }
            return inStock ? cb.greaterThan(root.get("stockQuantity"), 0) : cb.lessThanOrEqualTo(root.get("stockQuantity"), 0);
        };
    }

    public static Specification<Product> withFilters(String keyword, Long categoryId, Double minPrice, Double maxPrice,
            Boolean promotionOnly, Boolean inStock) {
        return Specification.where(keyword(keyword))
                .and(categoryId(categoryId))
                .and(minPrice(minPrice))
                .and(maxPrice(maxPrice))
                .and(promotionOnly(promotionOnly))
                .and(inStock(inStock));
    }
}
