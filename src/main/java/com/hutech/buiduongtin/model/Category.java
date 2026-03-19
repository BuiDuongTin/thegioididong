package com.hutech.buiduongtin.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Icon Bootstrap Icons, ví dụ: "bi-phone", "bi-laptop"
    private String icon;

    // Ảnh đại diện của danh mục
    private String image;

    // Danh mục cha (null nếu là cấp 1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parentCategory;

    // Danh mục con
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("name ASC")
    private List<Category> children = new ArrayList<>();

    // Danh sách sản phẩm của danh mục này (dùng cho menu preview)
    @OneToMany(mappedBy = "category")
    @OrderBy("id DESC") // Lấy sản phẩm mới nhất
    private List<Product> products = new ArrayList<>();

    // Constructor tiện lợi cho DataInitializer
    public Category(String name, String icon, Category parentCategory) {
        this.name = name;
        this.icon = icon;
        this.parentCategory = parentCategory;
    }
}
