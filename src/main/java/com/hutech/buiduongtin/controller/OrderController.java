package com.hutech.buiduongtin.controller;

import com.hutech.buiduongtin.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final com.hutech.buiduongtin.repository.OrderRepository orderRepository;
    private final com.hutech.buiduongtin.service.MomoService momoService;

    @GetMapping("/checkout")
    public String checkout() {
        return "cart/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(@RequestParam("customerName") String customerName,
            @RequestParam("shippingAddress") String shippingAddress,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("note") String note,
            @RequestParam(value = "discountCode", required = false) String discountCode,
            @RequestParam(value = "usePoints", required = false, defaultValue = "false") boolean usePoints,
            @RequestParam("paymentMethod") String paymentMethod,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        com.hutech.buiduongtin.model.Order savedOrder = orderService.createOrder(customerName, shippingAddress,
                phoneNumber, note, discountCode, usePoints, paymentMethod);

        if ("MOMO".equals(paymentMethod)) {
            try {
                String payUrl = momoService.createPayment(
                        savedOrder.getId().toString(),
                        savedOrder.getTotalPrice(),
                        "Thanh toan don hang #" + savedOrder.getId());
                if (payUrl != null && !payUrl.isEmpty()) {
                    return "redirect:" + payUrl;
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle error -> maybe redirect to cart with error
                return "redirect:/cart?error=momo";
            }
        }

        redirectAttributes.addFlashAttribute("order", savedOrder);
        return "redirect:/order/confirmation";
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Your order has been successfully placed.");
        return "cart/order-confirmation";
    }

    @GetMapping("/history")
    public String orderHistory(@RequestParam(value = "phone", required = false) String phone, Model model,
            java.security.Principal principal) {
        if (principal != null) {
            java.util.List<com.hutech.buiduongtin.model.Order> orders = orderRepository
                    .findByUserUsernameOrderByIdDesc(principal.getName());
            model.addAttribute("orders", orders);
            model.addAttribute("phone", principal.getName() + " (Tài khoản)");
            return "order/history";
        }
        if (phone != null && !phone.trim().isEmpty()) {
            java.util.List<com.hutech.buiduongtin.model.Order> orders = orderRepository
                    .findByPhoneNumberOrderByIdDesc(phone);
            model.addAttribute("orders", orders);
            model.addAttribute("phone", phone);
        }
        return "order/history";
    }

    @GetMapping("/points")
    @org.springframework.web.bind.annotation.ResponseBody
    public int getAccountPoints(@RequestParam("phone") String phone, java.security.Principal principal) {
        if (principal != null) {
            return orderRepository.getAvailablePointsByUsername(principal.getName());
        }
        if (phone == null || phone.trim().isEmpty()) {
            return 0;
        }
        return orderRepository.getAvailablePoints(phone);
    }
}
