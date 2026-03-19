package com.hutech.buiduongtin.service;

import com.hutech.buiduongtin.model.CartItem;
import com.hutech.buiduongtin.model.Product;
import com.hutech.buiduongtin.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {
    private List<CartItem> cartItems = new ArrayList<>();

    @Autowired
    private ProductRepository productRepository;

    public void addToCart(Long productId) {
        if (productId == null)
            throw new IllegalArgumentException("Product ID cannot be null");
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        for (CartItem item : cartItems) {
            if (item.getProductId() != null && item.getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }

        cartItems.add(new CartItem(product.getId(), product.getName(), product.getRealPrice(), 1, product.getImage()));
    }

    public void updateQuantity(Long productId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(productId);
            return;
        }
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProductId().equals(productId));
    }

    public void clearCart() {
        cartItems.clear();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public double getTotal() {
        return cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    public int getCartCount() {
        return cartItems.stream().mapToInt(CartItem::getQuantity).sum();
    }
}
// end of file
