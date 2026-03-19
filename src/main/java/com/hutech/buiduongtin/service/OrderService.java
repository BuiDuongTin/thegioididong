package com.hutech.buiduongtin.service;

import com.hutech.buiduongtin.model.CartItem;
import com.hutech.buiduongtin.model.Order;
import com.hutech.buiduongtin.model.OrderDetail;
import com.hutech.buiduongtin.model.Product;
import com.hutech.buiduongtin.model.User;
import com.hutech.buiduongtin.repository.OrderDetailRepository;
import com.hutech.buiduongtin.repository.OrderRepository;
import com.hutech.buiduongtin.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartService cartService;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final com.hutech.buiduongtin.repository.VoucherRepository voucherRepository;

    public Order createOrder(String customerName, String shippingAddress, String phoneNumber, String note,
            String discountCode, boolean usePoints, String paymentMethod) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return null; // Handle empty cart
        }

        double subtotal = cartService.getTotal();
        int totalQty = cartService.getCartCount();
        double shippingFee = 30000;

        if (subtotal > 1000000 && totalQty >= 2) {
            shippingFee = 0;
        }

        int earnedPoints = (int) (subtotal / 10000);
        int pointsUsed = 0;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            currentUser = userService.findByUsername(auth.getName());
        }

        if (usePoints) {
            int availablePoints = 0;
            if (currentUser != null) {
                availablePoints = orderRepository.getAvailablePointsByUsername(currentUser.getUsername());
            } else {
                availablePoints = orderRepository.getAvailablePoints(phoneNumber);
            }
            if (availablePoints >= 10) {
                shippingFee = Math.max(0, shippingFee - 10000);
                pointsUsed = 10;
            }
        }

        double discount = 0;
        // Check for voucher code first
        if (discountCode != null && !discountCode.trim().isEmpty()) {
            var optVoucher = voucherRepository.findByCode(discountCode.trim());
            if (optVoucher.isPresent()) {
                var v = optVoucher.get();
                // Ensure voucher unused and belongs to current user
                if (!v.isUsed() && currentUser != null && v.getUser() != null
                        && v.getUser().getUsername().equals(currentUser.getUsername())) {
                    discount = v.getAmount();
                    v.setUsed(true);
                    v.setUsedAt(LocalDateTime.now());
                    voucherRepository.save(v);
                }
            } else if ("SALE50".equalsIgnoreCase(discountCode)) {
                discount = 50000;
            }
        }

        double finalTotal = subtotal + shippingFee - discount;
        if (finalTotal < 0)
            finalTotal = 0;

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setNote(note);
        order.setTotalPrice(finalTotal);
        order.setEarnedPoints(earnedPoints);
        order.setUsedPoints(pointsUsed);
        order.setPaymentMethod(paymentMethod);

        if (currentUser != null) {
            order.setUser(currentUser);
        }

        Order savedOrder = orderRepository.save(order);

        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());

            Long pid = item.getProductId();
            if (pid == null)
                continue;
            Product product = productRepository.findById(pid).orElse(null);
            detail.setProduct(product);

            orderDetailRepository.save(detail);

            // Giảm số lượng tồn kho
            if (product != null) {
                int currentStock = product.getStockQuantity();
                int newStock = Math.max(0, currentStock - item.getQuantity());
                product.setStockQuantity(newStock);
                productRepository.save(product);
            }
        }

        cartService.clearCart();
        return savedOrder;
    }
}
