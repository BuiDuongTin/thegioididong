package com.hutech.buiduongtin.controller;

import com.hutech.buiduongtin.model.OtpToken;
import com.hutech.buiduongtin.model.User;
import com.hutech.buiduongtin.model.Voucher;
import com.hutech.buiduongtin.repository.OtpTokenRepository;
import com.hutech.buiduongtin.repository.OrderRepository;
import com.hutech.buiduongtin.repository.VoucherRepository;
import com.hutech.buiduongtin.service.MailService;
import com.hutech.buiduongtin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderRepository orderRepository;
    private final VoucherRepository voucherRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final MailService mailService;

    // Trang đăng nhập
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Bạn đã đăng xuất thành công.");
        }
        return "login";
    }

    // Trang đăng ký
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại!");
            return "register";
        }
        userService.save(user);
        return "redirect:/login?registered=true";
    }

    @GetMapping("/user/points")
    public String userPointsPage(java.security.Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        int availablePoints = orderRepository.getAvailablePointsByUsername(principal.getName());
        model.addAttribute("availablePoints", availablePoints);
        model.addAttribute("canUsePoints", availablePoints >= 10);
        return "user/points";
    }

    /**
     * Initiate redeem: create OTP token and send email with OTP. Voucher is created only after OTP verification.
     */
    @PostMapping("/user/redeem")
    public String redeemVoucherRequest(@RequestParam("amount") int amount, java.security.Principal principal,
                                       org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (principal == null) {
            return "redirect:/login";
        }
        int requiredPoints = Math.max(0, amount / 2000);
        int availablePoints = orderRepository.getAvailablePointsByUsername(principal.getName());
        if (availablePoints < requiredPoints) {
            redirectAttrs.addFlashAttribute("error", "Không đủ điểm để đổi voucher này.");
            return "redirect:/user/points";
        }

        User user = userService.findByUsername(principal.getName());

        // create OTP
        String otp = String.format("%06d", (int) (Math.random() * 900000) + 100000);
        OtpToken token = new OtpToken();
        token.setCode(otp);
        token.setAmount(amount);
        token.setRequiredPoints(requiredPoints);
        token.setUsed(false);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        token.setUser(user);
        otpTokenRepository.save(token);

        // send email via MailService
        String subject = "Mã OTP xác nhận đổi voucher";
        String text = "Mã OTP của bạn: " + otp + "\nSố tiền voucher: " + amount + "\nMã sẽ hết hạn sau 10 phút." +
                "\nVui lòng nhập mã tại: " + "http://localhost:8080/user/verify-otp?tokenId=" + token.getId();
        mailService.sendOtp(user.getEmail(), subject, text);

        redirectAttrs.addFlashAttribute("info", "Mã OTP đã được gửi tới email của bạn. Vui lòng kiểm tra và nhập mã để hoàn tất.");
        return "redirect:/user/verify-otp?tokenId=" + token.getId();
    }

    @GetMapping("/user/verify-otp")
    public String verifyOtpPage(@RequestParam(value = "tokenId", required = false) Long tokenId,
                                Model model, java.security.Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("tokenId", tokenId);
        return "user/verify-otp";
    }

    @PostMapping("/user/verify-otp")
    public String verifyOtpSubmit(@RequestParam("tokenId") Long tokenId,
                                  @RequestParam("code") String code,
                                  java.security.Principal principal,
                                  org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        if (principal == null) {
            return "redirect:/login";
        }
        Optional<OtpToken> optional = otpTokenRepository.findByIdAndCode(tokenId, code);
        if (optional.isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Mã OTP không đúng.");
            return "redirect:/user/verify-otp?tokenId=" + tokenId;
        }
        OtpToken token = optional.get();
        if (token.isUsed()) {
            redirectAttrs.addFlashAttribute("error", "Mã OTP đã được sử dụng.");
            return "redirect:/user/points";
        }
        if (token.getExpiresAt() != null && LocalDateTime.now().isAfter(token.getExpiresAt())) {
            redirectAttrs.addFlashAttribute("error", "Mã OTP đã hết hạn.");
            return "redirect:/user/points";
        }

        // create voucher
        Voucher voucher = new Voucher();
        String voucherCode = java.util.UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase();
        voucher.setCode(voucherCode);
        voucher.setAmount(token.getAmount());
        voucher.setCreatedAt(LocalDateTime.now());
        voucher.setUser(token.getUser());
        voucherRepository.save(voucher);

        // create fake order to deduct points
        com.hutech.buiduongtin.model.Order redeemOrder = new com.hutech.buiduongtin.model.Order();
        redeemOrder.setCustomerName("Đổi voucher");
        redeemOrder.setPhoneNumber(null);
        redeemOrder.setTotalPrice(0);
        redeemOrder.setPaymentMethod("VOUCHER");
        redeemOrder.setPaymentStatus("REDEEMED");
        redeemOrder.setEarnedPoints(0);
        redeemOrder.setUsedPoints(token.getRequiredPoints());
        redeemOrder.setUser(token.getUser());
        orderRepository.save(redeemOrder);

        token.setUsed(true);
        otpTokenRepository.save(token);

        redirectAttrs.addFlashAttribute("message",
                "Đổi " + token.getRequiredPoints() + " điểm lấy voucher " + String.format("%,d", token.getAmount()) + "đ thành công! Mã: " + voucherCode);
        return "redirect:/user/points";
    }
}
