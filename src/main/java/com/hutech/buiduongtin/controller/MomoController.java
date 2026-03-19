package com.hutech.buiduongtin.controller;

import com.hutech.buiduongtin.model.Order;
import com.hutech.buiduongtin.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/momo")
@RequiredArgsConstructor
public class MomoController {
    private final OrderRepository orderRepository;

    @GetMapping("/return")
    public String momoReturn(
            @RequestParam("orderId") String orderId,
            @RequestParam("amount") String amount,
            @RequestParam("resultCode") String resultCode,
            @RequestParam("message") String message,
            RedirectAttributes redirectAttributes,
            Model model) {

        if ("0".equals(resultCode)) {
            // Success
            try {
                String dbOrderIdStr = orderId;
                if (orderId.contains("x")) {
                    dbOrderIdStr = orderId.substring(orderId.lastIndexOf('x') + 1);
                }
                Long id = Long.parseLong(dbOrderIdStr);
                Order order = orderRepository.findById(id).orElse(null);
                if (order != null) {
                    order.setPaymentStatus("PAID");
                    orderRepository.save(order);
                    redirectAttributes.addFlashAttribute("order", order);
                    return "redirect:/order/confirmation";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Handle failure or order not found
        model.addAttribute("error", "Payment failed or was canceled: " + message);
        return "cart/checkout-error";
    }

    @PostMapping("/notify")
    public void momoNotify(@RequestParam("orderId") String orderId,
            @RequestParam("resultCode") String resultCode) {
        // Here we silently handle IPN from MoMo in the background
        if ("0".equals(resultCode)) {
            try {
                String dbOrderIdStr = orderId;
                if (orderId.contains("x")) {
                    dbOrderIdStr = orderId.substring(orderId.lastIndexOf('x') + 1);
                }
                Long id = Long.parseLong(dbOrderIdStr);
                Order order = orderRepository.findById(id).orElse(null);
                if (order != null && !"PAID".equals(order.getPaymentStatus())) {
                    order.setPaymentStatus("PAID");
                    orderRepository.save(order);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
