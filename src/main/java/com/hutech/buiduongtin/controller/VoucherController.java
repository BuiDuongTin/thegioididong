package com.hutech.buiduongtin.controller;

import com.hutech.buiduongtin.model.Voucher;
import com.hutech.buiduongtin.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherRepository voucherRepository;

    @GetMapping("/voucher/validate")
    public ResponseEntity<?> validateVoucher(@RequestParam("code") String code, java.security.Principal principal) {
        Map<String, Object> resp = new HashMap<>();
        var opt = voucherRepository.findByCode(code.trim());
        if (opt.isEmpty()) {
            resp.put("valid", false);
            resp.put("message", "Mã voucher không tồn tại");
            return ResponseEntity.ok(resp);
        }
        Voucher v = opt.get();
        if (v.isUsed()) {
            resp.put("valid", false);
            resp.put("message", "Mã voucher đã được sử dụng");
            return ResponseEntity.ok(resp);
        }
        if (v.getUser() != null) {
            if (principal == null || !principal.getName().equals(v.getUser().getUsername())) {
                resp.put("valid", false);
                resp.put("message", "Mã voucher này không thuộc về bạn");
                return ResponseEntity.ok(resp);
            }
        }
        resp.put("valid", true);
        resp.put("amount", v.getAmount());
        resp.put("message", "Mã hợp lệ");
        return ResponseEntity.ok(resp);
    }
}
