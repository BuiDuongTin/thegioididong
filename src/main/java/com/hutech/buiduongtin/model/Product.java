package com.hutech.buiduongtin.model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
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

    // Helper: kiểm tra có khuyến mãi không (tương thích template cũ)
    public boolean isPromotion() {
        return "DISCOUNT".equals(promotionType) || "GIFT".equals(promotionType);
    }

    // Helper: Tính giá trị thực tế sau khi áp dụng các quyền lợi giảm giá
    public double getRealPrice() {
        if ("DISCOUNT".equals(promotionType) && discountPercent > 0) {
            return price - (price * discountPercent / 100.0);
        }
        return price;
    }

    // Alias cho template
    public boolean getIsPromotion() {
        return isPromotion();
    }
}
