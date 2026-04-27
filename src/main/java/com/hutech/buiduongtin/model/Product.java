package com.hutech.buiduongtin.model;

import com.hutech.buiduongtin.model.enums.PromotionType;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_category_id", columnList = "category_id"),
        @Index(name = "idx_products_promotion_type", columnList = "promotion_type")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private String description;
    private String image;

    // Loại khuyến mãi: NONE, DISCOUNT, GIFT
    @Column(name = "promotion_type", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'NONE'")
    private String promotionType = "NONE";

    // Phần trăm giảm giá (chỉ dùng khi promotionType = DISCOUNT)
    @Column(name = "discount_percent", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int discountPercent = 0;

    // Quà tặng kèm (chỉ dùng khi promotionType = GIFT)
    @Column(name = "gift_description", columnDefinition = "VARCHAR(255) DEFAULT ''")
    private String giftDescription = "";

    // Số lượng tồn kho
    @Column(name = "stock_quantity", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int stockQuantity = 0;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @PrePersist
    @PreUpdate
    private void normalizePromotionType() {
        this.promotionType = PromotionType.fromCode(this.promotionType).code();
    }

    public PromotionType getPromotionTypeEnum() {
        return PromotionType.fromCode(promotionType);
    }

    public void setPromotionType(String promotionType) {
        this.promotionType = PromotionType.fromCode(promotionType).code();
    }

    public void setPromotionType(PromotionType promotionType) {
        this.promotionType = promotionType == null ? PromotionType.NONE.code() : promotionType.code();
    }

    // Helper: kiểm tra có khuyến mãi không (tương thích template cũ)
    public boolean isPromotion() {
        return getPromotionTypeEnum().isActive();
    }

    // Helper: Tính giá trị thực tế sau khi áp dụng các quyền lợi giảm giá
    public double getRealPrice() {
        if (getPromotionTypeEnum() == PromotionType.DISCOUNT && discountPercent > 0) {
            return price - (price * discountPercent / 100.0);
        }
        return price;
    }

    // Alias cho template
    public boolean getIsPromotion() {
        return isPromotion();
    }
}
