package com.hutech.buiduongtin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private Long productId;
    private String name;
    private double price;
    private int quantity;
    private String image;
}
