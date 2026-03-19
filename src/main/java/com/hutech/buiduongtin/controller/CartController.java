package com.hutech.buiduongtin.controller;

import com.hutech.buiduongtin.model.User;
import com.hutech.buiduongtin.repository.OrderRepository;
import com.hutech.buiduongtin.service.CartService;
import com.hutech.buiduongtin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public String viewCart(Model model, java.security.Principal principal) {
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPrice", cartService.getTotal());

        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                model.addAttribute("loggedInPhone", user.getPhone() != null ? user.getPhone() : "");
                model.addAttribute("loggedInPoints", orderRepository.getAvailablePointsByUsername(user.getUsername()));
            }
        }

        return "cart/cart-view";
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Long id) {
        cartService.addToCart(id);
        return "redirect:/cart";
    }

    @GetMapping("/add-stay/{id}")
    public String addToCartAndStay(@PathVariable Long id, jakarta.servlet.http.HttpServletRequest request) {
        cartService.addToCart(id);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity) {
        cartService.updateQuantity(productId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }
}
